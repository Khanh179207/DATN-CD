import axios from 'axios';

const GROQ_API_KEY = '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'; 

export const chatWithAIChef = async (chatHistory, newText, dbContext = []) => {
  try {
    // 1. Chuẩn bị "Bộ nhớ tạm" từ Database
    const recipesFound = dbContext.map(r => `- ID: ${r.id}, TÊN: ${r.title}`).join('\n');

    const systemPrompt = {
      role: "system",
      content: `Bạn là Gomet AI - Siêu đầu bếp có tư duy phản biện.
      
      DỮ LIỆU THỰC TẾ TỪ HỆ THỐNG GOMET:
      ${recipesFound || "Hiện tại chưa có bài viết nào khớp hoàn toàn."}

      QUY TẮC THÔNG MINH (BẮT BUỘC):
      1. PHÂN TÍCH Ý ĐỊNH: Nếu user hỏi câu dài (vd: "vậy món phở bò"), hãy tự lọc lấy từ khóa "phở bò" để đối chiếu với danh sách TÊN ở trên.
      2. GỢI Ý LIÊN QUAN: Nếu không có món khớp 100%, hãy tìm món có nguyên liệu tương tự. 
         Vd: User hỏi "phở bò" nhưng DB chỉ có "bún bò", hãy nói: "Tôi chưa có phở bò, nhưng sếp thử Bún Bò [LINK:ID] này nhé!".
      3. CẤU TRÚC PHẢN HỒI:
         - Bước 1: Trả lời ngắn gọn ý định của user.
         - Bước 2: Trình bày công thức bằng Markdown (Dùng **in đậm**, *nghiêng*, danh sách có dấu chấm).
         - Bước 3: Luôn gắn [LINK:ID] ngay sau tên món ăn nếu món đó có trong danh sách DỮ LIỆU THỰC TẾ phía trên.
      4. PHONG CÁCH: Thân thiện, dùng emoji, xưng "Tôi" và gọi người dùng là "Sếp".
      5. GIỚI HẠN: Tuyệt đối không bịa ra ID. Nếu món không có trong DB, không được dùng tag [LINK:xx].`
    };

    // 2. Lọc lịch sử chat (Chỉ lấy 6 câu gần nhất để AI không bị loãng ngữ cảnh)
    const formattedHistory = chatHistory.slice(-6).map(msg => ({
      role: msg.isMine ? "user" : "assistant",
      content: msg.text
    }));

    // 3. Gọi Groq với Model Llama 3.3 70B (Đỉnh cao về suy luận)
    const response = await axios.post('https://api.groq.com/openai/v1/chat/completions', {
      model: "llama-3.3-70b-versatile", 
      messages: [
        systemPrompt, 
        ...formattedHistory, 
        { role: "user", content: newText }
      ],
      temperature: 0.4, // Giảm xuống 0.4 để AI cực kỳ tập trung vào dữ liệu DB, bớt "bay bổng"
      max_tokens: 1500, // Cho phép trả lời dài hơn để viết công thức chi tiết
      top_p: 0.9
    }, {
      headers: { 
        'Authorization': `Bearer ${GROQ_API_KEY}`, 
        'Content-Type': 'application/json' 
      }
    });

    return response.data.choices[0].message.content;

  } catch (error) {
    console.error("AI Error:", error.response?.data || error.message);
    return "Đầu bếp AI đang đi chợ mua thêm gia vị, sếp nhắn lại sau 1 phút nhé! 👨‍🍳";
  }
}