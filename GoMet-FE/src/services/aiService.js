import axios from 'axios';
import i18n from '@/i18n';

const GROQ_API_KEY = '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'; 

export const chatWithAIChef = async (chatHistory, newText, dbContext = []) => {
  try {
    const recipesFound = dbContext
      .map(r => `- ID: ${r.id}, ${i18n.global.t('chat.ai_recipe_label')}: ${r.title}`)
      .join('\n');

    const systemPrompt = {
      role: "system",
      content: i18n.global.t('chat.ai_system_prompt', {
        recipes: recipesFound || i18n.global.t('chat.ai_no_matching_posts')
      })
    };

    const formattedHistory = chatHistory.slice(-6).map(msg => ({
      role: msg.isMine ? "user" : "assistant",
      content: msg.text
    }));

    const response = await axios.post('https://api.groq.com/openai/v1/chat/completions', {
      model: "llama-3.3-70b-versatile", 
      messages: [
        systemPrompt, 
        ...formattedHistory, 
        { role: "user", content: newText }
      ],
      temperature: 0.4,
      max_tokens: 1500,
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
    return i18n.global.t('chat.ai_busy_fallback');
  }
}