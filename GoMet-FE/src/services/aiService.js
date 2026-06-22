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

    // 2. CHUẨN BỊ NGỮ CẢNH TỪ DATABASE (Ép AI phải biết rõ web đang có gì)
    const recipesFound = dbContext.map(r => `- Món: ${r.title} (Mã ID: ${r.id})`).join('\n');

    // 🔥 NÂNG CẤP PROMPT: Dạy AI cách suy luận thông minh và khắt khe hơn
    const systemPrompt = {
      role: "system",
      content: `Bạn là Gomet AI - Siêu đầu bếp ảo của hệ thống ẩm thực GoMet. Bạn rất thông minh, hài hước và am hiểu tâm lý thực khách.
      
      📋 THỰC ĐƠN ĐANG CÓ TRÊN GOMET NGÀY HÔM NAY:
      ${recipesFound || "Hiện chưa tải được món nào từ hệ thống."}

      🎯 NHIỆM VỤ CỦA BẠN:
      1. Lắng nghe và phân tích ý định người dùng (Họ tìm nguyên liệu gì? Đang có cảm xúc/thời tiết ra sao? Thích ăn vặt hay ăn no?).
      2. ĐỐI CHIẾU ý định đó với "THỰC ĐƠN ĐANG CÓ" ở trên để tìm ra 1-2 món PHÙ HỢP NHẤT.
      3. NẾU CÓ MÓN PHÙ HỢP: Hãy giới thiệu thật hấp dẫn và BẮT BUỘC chèn cú pháp [LINK:Mã ID] ngay sát sau tên món.
      4. NẾU KHÔNG CÓ MÓN NÀO KHỚP: Hãy xin lỗi khéo léo và CHỦ ĐỘNG GỢI Ý các món đang có trong thực đơn để khách không bị hụt hẫng.
      5. TUYỆT ĐỐI KHÔNG ĐƯỢC bịa ra hoặc khuyên nấu món ăn KHÔNG CÓ trong "THỰC ĐƠN ĐANG CÓ".

      ✍️ QUY TẮC TRÌNH BÀY:
      - Xưng "Tôi", gọi người dùng là "Bạn".
      - Câu văn ngắn gọn, dùng Markdown (in đậm tên món, gạch đầu dòng) để dễ đọc.
      - Chèn các Emoji ẩm thực sinh động (🍲, 🥘, 🥗...).
      
      Ví dụ mẫu chuẩn: "Trời mưa lạnh thế này, bạn thử làm ngay món **Lẩu Thái Hải Sản** [LINK:12] cay nồng này nhé! Đảm bảo ấm bụng luôn! 🍲"`
    };

    // 3. Lọc lịch sử chat (lấy 6 câu gần nhất để AI nhớ mạch câu chuyện)
    const formattedHistory = chatHistory.slice(-6).map(msg => ({
      role: msg.isMine ? "user" : "assistant",
      content: msg.text
    }));

    // 4. Gửi yêu cầu lên Spring Boot (Máy chủ trung gian)
    const response = await axios.post(API_BASE_URL, {
      model: "llama-3.3-70b-versatile", 
      messages: [
        systemPrompt, 
        ...formattedHistory, 
        { role: "user", content: newText }
      ],
      // Hạ nhiệt độ xuống 0.5 để AI bám sát thực tế hơn, phân tích logic hơn và ít "bốc phét"
      temperature: 0.5, 
      max_tokens: 1000
    }, {
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}` 
      }
    });

    // 5. Trả về nội dung tin nhắn đã được AI trau chuốt
    return response.data.choices[0].message.content;

  } catch (error) {
    // Xử lý lỗi 403 (Token lỏ)
    if (error.response?.status === 403 || error.response?.status === 401) {
      return "Bạn ơi, phiên đăng nhập hết hạn rồi, bạn đăng nhập lại để tôi nhận diện nhé! 👨‍🍳";
    }
    
    // Xử lý lỗi hệ thống (Network, Groq Server)
    console.error("AI Proxy Error:", error.response?.data || error.message);
    return "Đầu bếp AI đang bận xào rau, bạn thử lại sau vài giây nhé! 👨‍🍳";
  }
}