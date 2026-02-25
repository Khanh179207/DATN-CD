/**
 * GoMet AI Service — OpenAI ChatGPT integration
 * Used by: MiniChatBox (AI Chef assistant) & MealPlanPage (auto-fill meal plan)
 */

const OPENAI_API_URL = 'https://api.openai.com/v1/chat/completions'
const API_KEY = import.meta.env.VITE_OPENAI_API_KEY

/** Base fetch helper */
async function callOpenAI(messages, options = {}) {
  if (!API_KEY) {
    throw new Error('OPENAI_API_KEY_MISSING')
  }
  const response = await fetch(OPENAI_API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${API_KEY}`,
    },
    body: JSON.stringify({
      model: 'gpt-4o-mini',
      messages,
      temperature: 0.7,
      max_tokens: options.maxTokens ?? 800,
      ...options,
    }),
  })

  if (!response.ok) {
    const err = await response.json().catch(() => ({}))
    throw new Error(err?.error?.message || `OpenAI API error: ${response.status}`)
  }

  const data = await response.json()
  return data.choices?.[0]?.message?.content?.trim() ?? ''
}

// ─────────────────────────────────────────────
// 1. AI CHEF CHAT
// ─────────────────────────────────────────────

const CHEF_SYSTEM_PROMPT = `Bạn là Gomet AI 🤖 — trợ lý ẩm thực thông minh của nền tảng chia sẻ công thức nấu ăn GOMET.
Nhiệm vụ của bạn:
- Trả lời câu hỏi về công thức nấu ăn, nguyên liệu, kỹ thuật nấu ăn bằng tiếng Việt (hoặc tiếng Anh nếu người dùng hỏi bằng tiếng Anh).
- Gợi ý món ăn dựa trên nguyên liệu sẵn có, khẩu vị hoặc chế độ ăn.
- Giải thích cách thay thế nguyên liệu, điều chỉnh khẩu phần, hoặc mẹo nấu ăn.
- Chia sẻ thông tin dinh dưỡng cơ bản khi được hỏi.
Phong cách: Thân thiện, nhiệt tình, giàu kiến thức, súc tích (không dài dòng). Dùng emoji tiết kiệm để tăng thân thiện. Luôn đưa ra câu trả lời có ích và cụ thể.`

/**
 * Send a message to Gomet AI Chef and get a response.
 * @param {Array<{role: 'user'|'assistant', content: string}>} history - conversation history
 * @param {string} userMessage - the latest message from the user
 * @returns {Promise<string>} AI reply
 */
export async function chatWithAIChef(history, userMessage) {
  const messages = [
    { role: 'system', content: CHEF_SYSTEM_PROMPT },
    ...history.map(m => ({ role: m.isMine ? 'user' : 'assistant', content: m.text })),
    { role: 'user', content: userMessage },
  ]
  return callOpenAI(messages, { maxTokens: 600 })
}

// ─────────────────────────────────────────────
// 2. AI MEAL PLAN GENERATOR
// ─────────────────────────────────────────────

const MEAL_PLAN_SYSTEM_PROMPT = `Bạn là chuyên gia dinh dưỡng và đầu bếp của GOMET. Hãy tạo thực đơn 7 ngày đầy đủ (thứ Hai đến Chủ Nhật) với bữa sáng, trưa và tối.
Quy tắc:
- Đa dạng món, cân bằng dinh dưỡng, phù hợp ẩm thực Việt Nam và quốc tế.
- Tên món phải ngắn gọn, rõ ràng (tối đa 5 từ).
- Category là một trong: Vietnamese, Healthy, Western, Asian, Dessert.
- Trả về DUY NHẤT một JSON object hợp lệ theo schema sau (không thêm gì ngoài JSON):
{
  "days": [
    {
      "name": "MON",
      "meals": {
        "breakfast": { "name": "Tên món", "cat": "Category" },
        "lunch":     { "name": "Tên món", "cat": "Category" },
        "dinner":    { "name": "Tên món", "cat": "Category" }
      }
    },
    ... (TUE, WED, THU, FRI, SAT, SUN)
  ]
}`

/** Map category names to Unsplash photo IDs for a nice image */
const CAT_IMAGES = {
  Vietnamese: 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=600',
  Healthy:    'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600',
  Western:    'https://images.unsplash.com/photo-1600891964092-4316c288032e?w=600',
  Asian:      'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=600',
  Dessert:    'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=600',
}

function getImageForCat(cat) {
  return CAT_IMAGES[cat] ?? CAT_IMAGES['Healthy']
}

/**
 * Generate a full 7-day meal plan using AI.
 * @param {string} [preferences] - optional dietary preferences string
 * @returns {Promise<Array>} array of 7 day objects compatible with MealPlanPage weekData
 */
export async function generateAIMealPlan(preferences = '') {
  const userMsg = preferences
    ? `Tạo thực đơn 7 ngày. Sở thích/yêu cầu: ${preferences}`
    : 'Tạo thực đơn 7 ngày đa dạng, cân bằng dinh dưỡng.'

  const messages = [
    { role: 'system', content: MEAL_PLAN_SYSTEM_PROMPT },
    { role: 'user', content: userMsg },
  ]

  const raw = await callOpenAI(messages, { maxTokens: 1200, temperature: 0.8 })

  // Parse JSON — strip markdown fences if present
  const jsonStr = raw.replace(/```json\s*/gi, '').replace(/```/g, '').trim()
  const parsed = JSON.parse(jsonStr)

  const DAY_LABELS = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
  const DAY_DATES  = ['02', '03', '04', '05', '06', '07', '08']

  return (parsed.days ?? []).map((d, i) => ({
    name:    DAY_LABELS[i] ?? d.name,
    date:    DAY_DATES[i] ?? '00',
    isToday: i === 0,
    meals: {
      breakfast: d.meals?.breakfast ? { ...d.meals.breakfast, image: getImageForCat(d.meals.breakfast.cat) } : null,
      lunch:     d.meals?.lunch     ? { ...d.meals.lunch,     image: getImageForCat(d.meals.lunch.cat)     } : null,
      dinner:    d.meals?.dinner    ? { ...d.meals.dinner,    image: getImageForCat(d.meals.dinner.cat)    } : null,
    },
  }))
}
