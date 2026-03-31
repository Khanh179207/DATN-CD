import axios from 'axios';

// 🔥 API_BASE_URL: Gọi về Backend của sếp
const API_BASE_URL = 'http://localhost:8080/api/ai/chef-chat'; 

export const chatWithAIChef = async (chatHistory, newText, dbContext = []) => {
  try {
    // 1. LẤY TOKEN: Đây chính là "thẻ thông hành" để vượt qua lỗi 403
    // Sếp kiểm tra xem trong máy sếp lưu là 'token', 'jwt' hay 'accessToken' nhé!
    const token = localStorage.getItem('token'); 

    // 2. Chuẩn bị ngữ cảnh từ Database (giữ nguyên logic xịn của sếp)
    const recipesFound = dbContext.map(r => `- ID: ${r.id}, TÊN: ${r.title}`).join('\n');

    const systemPrompt = {
      role: "system",
      content: `Bạn là Gomet AI - Siêu đầu bếp có tư duy phản biện.
      
      DỮ LIỆU THỰC TẾ TỪ HỆ THỐNG GOMET:
      ${recipesFound || "Hiện tại chưa có bài viết nào khớp hoàn toàn."}

      QUY TẮC THÔNG MINH (BẮT BUỘC):
      1. PHÂN TÍCH Ý ĐỊNH: Nếu user hỏi câu dài, lọc lấy từ khóa đối chiếu danh sách TÊN.
      2. GỢI Ý LIÊN QUAN: Nếu không có món khớp 100%, tìm món tương tự và dùng tag [LINK:ID].
      3. CẤU TRÚC PHẢN HỒI: Markdown (in đậm, nghiêng, list), gắn [LINK:ID] sau tên món.
      4. PHONG CÁCH: Thân thiện, emoji, xưng "Tôi" và gọi "Sếp".
      5. GIỚI HẠN: Tuyệt đối không bịa ID.`
    };

    // 3. Lọc lịch sử chat
    const formattedHistory = chatHistory.slice(-6).map(msg => ({
      role: msg.isMine ? "user" : "assistant",
      content: msg.text
    }));

    // 4. Gọi về Backend kèm theo Header Authorization
    const response = await axios.post(API_BASE_URL, {
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
        'Content-Type': 'application/json',
        // 🔥 ĐÚT THẺ VÀO TÚI: Gửi Token lên để Spring Security không chặn 403 nữa
        'Authorization': `Bearer ${token}` 
      }
    });

    return response.data.choices[0].message.content;

  } catch (error) {
    // Nếu vẫn lỗi 403, sếp kiểm tra lại xem Token có bị null không
    if (error.response?.status === 403) {
      console.error("Lỗi 403: Sếp chưa đăng nhập hoặc Token hết hạn rồi!");
      return "Sếp ơi, hình như sếp chưa đăng nhập nên tôi không nấu ăn được! 👨‍🍳";
    }
    console.error("AI Proxy Error:", error.response?.data || error.message);
    return "Đầu bếp AI đang bận nấu đám cưới, sếp nhắn lại sau nhé! 👨‍🍳";
  }
}