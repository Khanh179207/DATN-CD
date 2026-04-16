import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/ai/chef-chat'; 

export const chatWithAIChef = async (chatHistory, newText, dbContext = []) => {
  try {
    // 1. LẤY TOKEN: Kiểm tra kỹ cả 2 trường hợp lưu 'token' lẻ hoặc lưu trong 'user'
    let token = localStorage.getItem('token'); 

    if (!token) {
      const userData = localStorage.getItem('user');
      if (userData) {
        try {
          const parsed = JSON.parse(userData);
          token = parsed.token;
        } catch (e) {
          console.error("Lỗi parse user data:", e);
        }
      }
    }

    // 🛑 CHẶN ĐỨNG NẾU KHÔNG CÓ TOKEN: Để tránh lỗi 403/401
    if (!token || token === 'null' || token === 'undefined') {
      return "Bạn ơi, bạn chưa đăng nhập nên tôi không biết bạn là ai để phục vụ rồi! 👨‍🍳";
    }

    // 2. Chuẩn bị ngữ cảnh từ Database
    const recipesFound = dbContext.map(r => `- ID: ${r.id}, TÊN: ${r.title}`).join('\n');

    const systemPrompt = {
      role: "system",
      content: `Bạn là Gomet AI - Siêu đầu bếp thân thiện.
      
      DỮ LIỆU THỰC TẾ:
      ${recipesFound || "Không có món nào khớp."}

      QUY TẮC:
      - Xưng "Tôi", gọi người dùng là "Bạn".
      - Dùng Markdown và Emoji.
      - Gắn [LINK:ID] ngay sau tên món có trong danh sách.`
    };

    // 3. Lọc lịch sử chat (lấy 6 câu gần nhất)
    const formattedHistory = chatHistory.slice(-6).map(msg => ({
      role: msg.isMine ? "user" : "assistant",
      content: msg.text
    }));

    // 4. Gửi yêu cầu với đầy đủ 'model' để fix lỗi 400 Bad Request
    const response = await axios.post(API_BASE_URL, {
      // 🔥 QUAN TRỌNG: Thêm model vào đây thì Groq mới chạy được
      model: "llama-3.3-70b-versatile", 
      messages: [
        systemPrompt, 
        ...formattedHistory, 
        { role: "user", content: newText }
      ],
      temperature: 0.5,
      max_tokens: 1000
    }, {
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}` 
      }
    });

    // 5. Trả về nội dung tin nhắn
    return response.data.choices[0].message.content;

  } catch (error) {
    // Xử lý lỗi 403 (Token lỏ)
    if (error.response?.status === 403 || error.response?.status === 401) {
      return "Bạn ơi, Token bị lỗi rồi, bạn đăng nhập lại để tôi nhận diện nhé! 👨‍🍳";
    }
    
    // Xử lý lỗi 400 (Nếu Groq vẫn báo thiếu gì đó)
    console.error("AI Proxy Error:", error.response?.data || error.message);
    return "Đầu bếp AI đang bị nghẹn, bạn thử lại sau vài giây nhé! 👨‍🍳";
  }
}