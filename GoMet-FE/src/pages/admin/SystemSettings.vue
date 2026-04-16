<template>
  <div class="settings-page-container">
    
    <div class="page-header anim-fade-down">
      <div class="header-content">
        <div class="title-wrapper">
          <div class="icon-box dark-gradient">
            <Settings :size="28" stroke-width="2.5" />
          </div>
          <div class="header-text">
            <h2 class="title">Vận Hành Hệ Thống</h2>
            <p class="subtitle">Quản lý trung tâm toàn bộ giao diện và cấu hình Website</p>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isLoading" class="loading-state">
      <Loader2 :size="48" class="spin-icon text-orange" />
      <p>Đang đồng bộ dữ liệu hệ thống...</p>
    </div>

    <div v-else class="settings-dashboard anim-fade-up">
      
      <section class="config-card highlight-card">
        <div class="card-header">
          <div class="icon-title"><Star :size="20" class="text-yellow" /> <h3>Bài Viết Nổi Bật (Hero Slider)</h3></div>
          <span class="badge yellow">HOT</span>
        </div>
        <div class="card-body">
          <p class="field-desc">Nhập ID của 3 bài viết nổi bật mà sếp muốn hiển thị lớn trên thanh trượt ở trang chủ.</p>
          <div class="hero-post-grid">
            <div v-for="n in 3" :key="n" class="post-id-input">
              <label>Vị trí số {{ n }}</label>
              <div class="input-wrapper focus-anim">
                <Hash :size="16" class="hash-icon" />
                <input 
                  type="number" 
                  v-model="formData['HERO_POST_' + n]" 
                  @input="handleInputDebounce('HERO_POST_' + n)" 
                  placeholder="Nhập Post ID" 
                />
              </div>
              <transition name="fade">
                <div v-if="previewData['HERO_POST_' + n]" class="post-preview-card">
                  <Loader2 v-if="previewData['HERO_POST_' + n].loading" :size="16" class="spin-icon text-gray" />
                  
                  <template v-else-if="previewData['HERO_POST_' + n].error">
                    <span class="text-red">{{ previewData['HERO_POST_' + n].errorMsg || '❌ Không tồn tại' }}</span>
                  </template>

                  <template v-else>
                    <img :src="previewData['HERO_POST_' + n].image" class="preview-thumb">
                    <div class="preview-info">
                      <div class="p-title">{{ previewData['HERO_POST_' + n].title }}</div>
                      <div class="p-author">{{ previewData['HERO_POST_' + n].author }}</div>
                    </div>
                  </template>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </section>

      <section class="config-card holiday-card">
        <div class="card-header">
          <div class="icon-title"><Gift :size="20" class="text-pink" /> <h3>Sự Kiện & Ngày Lễ</h3></div>
          <span class="badge pink">EVENT</span>
        </div>
        <div class="card-body">
          <div class="holiday-flex">
            <div class="holiday-info">
              <h4 class="holiday-label">Chế độ Mở Khóa Miễn Phí (Free Access)</h4>
              <p class="field-desc">Khi bật, tất cả bài viết sẽ được mở khóa cho mọi người xem không giới hạn (Bỏ qua giới hạn lượt xem ngày).</p>
            </div>
            <div class="toggle-container">
              <div class="lux-toggle" :class="{ 'active': formData.FREE_ACCESS_EVENT === 'TRUE' }" @click="toggleHoliday">
                <div class="toggle-handle"></div>
                <span class="toggle-text">{{ formData.FREE_ACCESS_EVENT === 'TRUE' ? 'ĐANG BẬT' : 'ĐANG TẮT' }}</span>
              </div>
            </div>
          </div>

          <div class="schedule-grid mt-4">
            <div class="datetime-group">
              <label>Thời gian Bắt đầu</label>
              <input type="datetime-local" v-model="formData.HOLIDAY_START" class="lux-datetime-input focus-anim-pink" />
            </div>
            <div class="datetime-group">
              <label>Thời gian Kết thúc</label>
              <input type="datetime-local" v-model="formData.HOLIDAY_END" class="lux-datetime-input focus-anim-pink" />
            </div>
          </div>
        </div>
      </section>

      <section class="config-card">
        <div class="card-header">
          <div class="icon-title"><CreditCard :size="20" class="text-orange" /> <h3>Bảng Giá Gói Premium</h3></div>
          <span class="badge orange">REVENUE</span>
        </div>
        <div class="card-body">
          <div class="pricing-grid">
            <div class="price-input-group" v-for="pkg in packages" :key="pkg.key">
              <label>{{ pkg.label }}</label>
              <div class="input-with-suffix focus-anim">
                <input type="number" v-model="formData[pkg.key]" :placeholder="pkg.placeholder" />
                <span class="suffix">VNĐ</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="config-card">
        <div class="card-header">
          <div class="icon-title"><Megaphone :size="20" class="text-green" /> <h3>Chiến Dịch Quảng Cáo Popup</h3></div>
          <span class="badge green">MARKETING</span>
        </div>
        <div class="card-body">
          <div class="ads-layout">
            <div class="ads-img-col">
              <div class="image-uploader-lux" @click="triggerUpload('adsInput')">
                <img v-if="formData.ADS_BANNER_IMG" :src="formData.ADS_BANNER_IMG" class="preview-img" />
                <div v-else class="upload-placeholder">
                  <UploadCloud :size="28" class="bounce-cloud" />
                  <span>Tải Ảnh Popup Ads</span>
                </div>
                <input type="file" ref="adsInput" class="hidden" accept="image/*" @change="e => handleImageUpload(e, 'ADS_BANNER_IMG')" />
              </div>
            </div>
            <div class="ads-info-col">
              <div class="input-group">
                <label>Đường link đích (Target URL)</label>
                <input type="text" v-model="formData.ADS_TARGET_URL" class="lux-input" placeholder="VD: https://shopee.vn/..." />
                <p class="input-hint">User sẽ được chuyển hướng khi click vào banner.</p>
              </div>
              <button class="btn-clear-ads danger-shake mt-4" @click="clearAds" v-if="formData.ADS_BANNER_IMG">
                <Trash2 :size="16"/> Gỡ bỏ quảng cáo
              </button>
            </div>
          </div>
        </div>
      </section>

    </div>

    <div class="floating-action-bar" :class="{ 'visible': !isLoading }">
      <div class="fab-inner">
        <button class="btn-cancel" @click="fetchConfigs" :disabled="isSaving">Hoàn tác</button>
        <button class="btn-save-lux" @click="saveConfigs" :disabled="isSaving">
          <Loader2 v-if="isSaving" :size="20" class="spin-icon" />
          <Save v-else :size="20" class="save-icon-glow" />
          <span>Lưu Thay Đổi</span>
        </button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Settings, CreditCard, Megaphone, UploadCloud, Save, Loader2, Star, Hash, Trash2, Gift } from 'lucide-vue-next'
import api from '@/services/api'
import { uploadMedia } from '@/services/uploadService'
import { toast } from '@/composables/useToast'

const adsInput = ref(null)
const isLoading = ref(true)
const isSaving = ref(false)

const packages = [
  { key: 'PREMIUM_PRICE_1_MONTH', label: 'Gói 1 Tháng', placeholder: '49000' },
  { key: 'PREMIUM_PRICE_12_MONTHS', label: 'Gói 1 Năm', placeholder: '399000' },
  { key: 'PREMIUM_PRICE_LIFETIME', label: 'Gói Vĩnh Viễn', placeholder: '999000' }
]

const formData = ref({
  HERO_POST_1: '', HERO_POST_2: '', HERO_POST_3: '',
  PREMIUM_PRICE_1_MONTH: '', PREMIUM_PRICE_12_MONTHS: '', PREMIUM_PRICE_LIFETIME: '',
  ADS_BANNER_IMG: '', ADS_TARGET_URL: '',
  FREE_ACCESS_EVENT: 'FALSE', 
  HOLIDAY_START: '', 
  HOLIDAY_END: ''
})

const previewData = ref({ HERO_POST_1: null, HERO_POST_2: null, HERO_POST_3: null })
let debounceTimer = null;

const fetchConfigs = async () => {
  isLoading.value = true
  try {
    const res = await api.get('/api/system-config')
    const configList = Array.isArray(res.data) ? res.data : (res.data?.data || [])
    configList.forEach(item => {
      const key = item.configKey || item.ConfigKey;
      if (formData.value[key] !== undefined) {
        formData.value[key] = item.configValue || item.ConfigValue || '';
        if (key.startsWith('HERO_POST_') && formData.value[key]) fetchPostPreview(key, formData.value[key]);
      }
    })
  } catch (error) { toast.error('Lỗi tải dữ liệu!'); } 
  finally { isLoading.value = false }
}

const toggleHoliday = () => {
  formData.value.FREE_ACCESS_EVENT = formData.value.FREE_ACCESS_EVENT === 'TRUE' ? 'FALSE' : 'TRUE';
}

const handleInputDebounce = (key) => {
  clearTimeout(debounceTimer);
  if (!formData.value[key]) { previewData.value[key] = null; return; }
  previewData.value[key] = { loading: true };
  debounceTimer = setTimeout(() => fetchPostPreview(key, formData.value[key]), 600);
}

// 🔥 ĐÃ SỬA: Chặn không cho gắn bài ẩn/chưa duyệt
const fetchPostPreview = async (key, postId) => {
  try {
    const res = await api.get(`/api/posts/${postId}`);
    const p = res.data?.data || res.data;

    // Kiểm tra trạng thái bài viết
    const isActive = Number(p.isActive ?? 1);
    const isApproved = Number(p.isApproved ?? 1);

    if (isActive !== 1 || isApproved !== 1) {
      let reason = 'Không hợp lệ';
      if (isActive === 1 && isApproved === 0) reason = 'đang chờ duyệt';
      else if (isActive === 0) reason = 'đang bị ẩn bởi tác giả';
      else if (isActive === -1) reason = 'đã bị Admin gỡ';

      // Hiển thị lỗi UI
      previewData.value[key] = { loading: false, error: true, errorMsg: `❌ Bài viết ${reason}` };
      
      // Bắn Toast báo cho Admin biết
      toast.warn(`Post ID #${postId} ${reason}, không thể chọn!`);
      
      // Reset input ngay lập tức để không cho phép lưu ID không hợp lệ vào DB
      formData.value[key] = '';
      return;
    }

    // Nếu bài viết sạch sẽ, cho phép hiển thị
    previewData.value[key] = {
      loading: false, error: false,
      title: p.title, image: p.media || p.image,
      author: p.authorName || p.account?.username
    };
  } catch (e) { 
    previewData.value[key] = { loading: false, error: true, errorMsg: '❌ Không tồn tại' }; 
    formData.value[key] = ''; // Xóa luôn nếu ID không tồn tại
  }
}

const handleImageUpload = async (event, key) => {
  const file = event.target.files[0];
  if (!file) return;
  toast.info('Đang tải ảnh...');
  try {
    formData.value[key] = await uploadMedia(file, 'system');
    toast.success('Thành công!');
  } catch (err) { toast.error('Lỗi tải ảnh!'); }
}

const clearAds = () => { formData.value.ADS_BANNER_IMG = ''; formData.value.ADS_TARGET_URL = ''; }
const triggerUpload = (refName) => { if (refName === 'adsInput') adsInput.value.click() }

const saveConfigs = async () => {
  isSaving.value = true
  try {
    const payload = {};
    for (const key in formData.value) payload[key] = String(formData.value[key]).trim();
    await api.put('/api/system-config/admin/update', payload);
    toast.success('🎉 Đã lưu cấu hình vận hành!');
  } catch (error) { toast.error('Lỗi lưu dữ liệu!'); } 
  finally { isSaving.value = false }
}

onMounted(fetchConfigs)
</script>

<style scoped lang="scss">
.holiday-card {
  border: 2px solid #fbcfe8;
  background: linear-gradient(to bottom right, #ffffff, #fff1f2);
}

.holiday-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.holiday-label {
  font-size: 1.1rem;
  font-weight: 800;
  color: #9d174d;
  margin-bottom: 8px;
}

/* LUXURY TOGGLE SWITCH */
.lux-toggle {
  width: 140px;
  height: 48px;
  background: #e2e8f0;
  border-radius: 100px;
  position: relative;
  cursor: pointer;
  transition: 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  display: flex;
  align-items: center;
  padding: 0 15px;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.1);

  &.active {
    background: #db2777;
    .toggle-handle { transform: translateX(85px); }
    .toggle-text { transform: translateX(-35px); color: white; }
  }

  .toggle-handle {
    width: 36px;
    height: 36px;
    background: white;
    border-radius: 50%;
    position: absolute;
    left: 6px;
    transition: 0.4s cubic-bezier(0.16, 1, 0.3, 1);
    box-shadow: 0 2px 10px rgba(0,0,0,0.2);
  }

  .toggle-text {
    font-size: 0.75rem;
    font-weight: 900;
    letter-spacing: 1px;
    color: #64748b;
    margin-left: auto;
    transition: 0.3s;
  }
}

.text-pink { color: #db2777; }
.badge.pink { background: #fbcfe8; color: #9d174d; }

/* Hẹn giờ */
.schedule-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 24px; padding-top: 24px;
  border-top: 1px dashed #fbcfe8;
}
.datetime-group label { display: block; font-weight: 700; color: #9d174d; margin-bottom: 8px; font-size: 0.95rem; }
.lux-datetime-input {
  width: 100%; padding: 14px 16px; border-radius: 12px; border: 2px solid #fce7f3;
  font-size: 1rem; font-weight: 600; color: #831843; outline: none; transition: 0.3s;
  background: white; font-family: inherit;
}
.focus-anim-pink:focus { border-color: #db2777; box-shadow: 0 0 0 4px rgba(219, 39, 119, 0.1); }


@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=Playfair+Display:wght@800&display=swap');

.settings-page-container {
  padding: 40px 50px;
  font-family: 'Plus Jakarta Sans', sans-serif;
  background-color: #f8fafc;
  min-height: 100vh;
  padding-bottom: 120px;
}

/* HEADER */
.page-header { margin-bottom: 40px; }
.title-wrapper { display: flex; align-items: center; gap: 20px; }
.icon-box.dark-gradient { 
  width: 60px; height: 60px; border-radius: 16px; 
  background: linear-gradient(135deg, #0f172a, #1e293b); 
  color: white; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 15px 25px -5px rgba(15, 23, 42, 0.4);
}
.title { font-family: 'Playfair Display', serif; font-size: 2.5rem; font-weight: 800; color: #0f172a; margin: 0; letter-spacing: -0.5px; }
.subtitle { color: #64748b; margin: 6px 0 0; font-size: 1.05rem; font-weight: 500; }

.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 100px 0; color: #64748b; font-weight: 600; gap: 16px; }
.text-orange { color: #ea580c; }
.spin-icon { animation: spin 1s linear infinite; }

/* CARDS */
.settings-dashboard { display: flex; flex-direction: column; gap: 32px; max-width: 1000px; margin: 0 auto; }
.config-card { 
  background: white; border-radius: 24px; border: 1px solid #f1f5f9;
  box-shadow: 0 10px 40px -10px rgba(0,0,0,0.03); overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}
.config-card:hover { transform: translateY(-3px); box-shadow: 0 15px 50px -10px rgba(0,0,0,0.06); }

.card-header { 
  padding: 20px 24px; border-bottom: 1px solid #f1f5f9; background: #f8fafc;
  display: flex; justify-content: space-between; align-items: center;
}
.icon-title { display: flex; align-items: center; gap: 12px; }
.icon-title h3 { font-size: 1.1rem; font-weight: 800; color: #0f172a; margin: 0; }
.text-yellow { color: #eab308; } .text-orange { color: #ea580c; } .text-green { color: #10b981; }
.text-gray { color: #94a3b8; } .text-red { color: #ef4444; font-weight: 600; font-size: 0.9rem;}
.badge { font-size: 0.75rem; font-weight: 800; padding: 4px 12px; border-radius: 100px; letter-spacing: 1px; }
.badge.yellow { background: #fef08a; color: #a16207; }
.badge.orange { background: #ffedd5; color: #c2410c; }
.badge.green { background: #dcfce7; color: #15803d; }

.card-body { padding: 32px; }
.field-desc { margin: 0 0 24px 0; color: #64748b; font-size: 0.95rem; font-weight: 500; }

/* FOCUS ANIMATION CHO INPUT */
.focus-anim { position: relative; border-radius: 12px; }
.focus-anim::after {
  content: ''; position: absolute; inset: -2px; border-radius: 14px;
  background: linear-gradient(90deg, #ea580c, #f59e0b, #ea580c);
  background-size: 200% auto; z-index: -1; opacity: 0; transition: opacity 0.3s;
}
.focus-anim:focus-within::after { opacity: 1; animation: shine 2s linear infinite; }
@keyframes shine { to { background-position: 200% center; } }

/* HIGHLIGHT POST GRID */
.highlight-card { border: 2px solid #fef08a; }
.hero-post-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.post-id-input label { display: block; font-weight: 700; color: #0f172a; margin-bottom: 8px; font-size: 0.95rem; }
.input-wrapper { display: flex; align-items: center; background: #f8fafc; border-radius: 12px; border: 2px solid #e2e8f0; transition: 0.2s; }
.hash-icon { color: #94a3b8; margin-left: 16px; }
.input-wrapper input { 
  width: 100%; padding: 14px 16px; border: none; background: transparent;
  font-size: 1.1rem; font-weight: 700; color: #0f172a; outline: none;
}
.input-wrapper:focus-within { border-color: transparent; background: white; }

/* THẺ HIỂN THỊ XEM TRƯỚC (LIVE PREVIEW CARD) */
.post-preview-card {
  margin-top: 12px; padding: 12px;
  background: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 12px;
  display: flex; align-items: center; gap: 12px;
}
.preview-thumb { width: 44px; height: 44px; border-radius: 8px; object-fit: cover; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
.preview-info { display: flex; flex-direction: column; overflow: hidden; }
.p-title { font-weight: 700; font-size: 0.9rem; color: #0f172a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.p-author { font-size: 0.75rem; color: #64748b; margin-top: 2px; }

/* PRICING GRID */
.pricing-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.price-input-group label { display: block; font-weight: 700; color: #0f172a; margin-bottom: 8px; font-size: 0.95rem; }
.input-with-suffix { display: flex; align-items: center; background: white; border-radius: 12px; border: 2px solid #e2e8f0; transition: 0.2s; }
.input-with-suffix input { 
  width: 100%; padding: 14px 16px; border: none; background: transparent;
  font-size: 1.1rem; font-weight: 700; color: #ea580c; outline: none;
}
.input-with-suffix:focus-within { border-color: transparent; }
.suffix { font-weight: 800; color: #94a3b8; font-size: 0.9rem; padding-right: 16px; }

/* ADS LAYOUT */
.ads-layout { display: grid; grid-template-columns: 1.2fr 1.8fr; gap: 32px; align-items: start; }
.image-uploader-lux { 
  position: relative; width: 100%; height: 260px; border-radius: 16px; 
  background: #f1f5f9; border: 2px dashed #cbd5e1; display: flex; 
  align-items: center; justify-content: center; cursor: pointer; overflow: hidden;
  transition: 0.3s;
}
.image-uploader-lux:hover { border-color: #10b981; background: #f0fdf4; }
.bounce-cloud { animation: bounce 2s infinite; color: #94a3b8; }
@keyframes bounce { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-5px); } }

.preview-img { width: 100%; height: 100%; object-fit: cover; }
.upload-placeholder { display: flex; flex-direction: column; align-items: center; gap: 12px; font-weight: 600; color: #64748b; }
.glass-overlay { position: absolute; inset: 0; background: rgba(15, 23, 42, 0.6); color: white; display: flex; align-items: center; justify-content: center; font-weight: 700; opacity: 0; transition: 0.3s; backdrop-filter: blur(4px); }
.image-uploader-lux:hover .glass-overlay { opacity: 1; }
.hidden { display: none; }

.lux-input { 
  width: 100%; padding: 16px; border: 2px solid #e2e8f0; border-radius: 12px; 
  font-size: 1rem; font-weight: 600; color: #0f172a; outline: none; transition: 0.2s;
}
.lux-input:focus { border-color: transparent; }
.input-hint { font-size: 0.85rem; color: #94a3b8; margin-top: 8px; font-style: italic; }

.mt-4 { margin-top: 24px; }
.btn-clear-ads { background: #fef2f2; color: #ef4444; border: 1px solid #fecaca; padding: 12px 20px; border-radius: 10px; font-weight: 700; cursor: pointer; display: flex; align-items: center; gap: 10px; font-size: 0.95rem; transition: 0.2s; }
.btn-clear-ads:hover { background: #ef4444; color: white; border-color: #ef4444; }
.danger-shake:hover { animation: shake 0.4s cubic-bezier(.36,.07,.19,.97) both; }
@keyframes shake { 10%, 90% { transform: translate3d(-1px, 0, 0); } 20%, 80% { transform: translate3d(2px, 0, 0); } 30%, 50%, 70% { transform: translate3d(-2px, 0, 0); } 40%, 60% { transform: translate3d(2px, 0, 0); } }

/* FLOAT ACTION BAR */
.floating-action-bar { 
  position: fixed; bottom: -100px; left: 0; width: 100%; 
  pointer-events: none; display: flex; justify-content: center; 
  z-index: 1000; transition: bottom 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}
.floating-action-bar.visible { bottom: 40px; }
.fab-inner {
  pointer-events: auto; background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(16px);
  padding: 12px 24px; border-radius: 100px; display: flex; gap: 16px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.15); border: 1px solid rgba(255,255,255,0.6);
}
.btn-cancel { padding: 14px 28px; background: transparent; border: none; font-weight: 700; color: #64748b; cursor: pointer; transition: 0.2s; border-radius: 100px; }
.btn-cancel:hover { background: #f1f5f9; color: #0f172a; }
.btn-save-lux { 
  padding: 14px 40px; background: linear-gradient(135deg, #0f172a, #1e293b); color: white; 
  border: none; border-radius: 100px; font-weight: 800; font-size: 1.05rem; cursor: pointer; 
  display: flex; align-items: center; gap: 10px; box-shadow: 0 10px 20px -5px rgba(15, 23, 42, 0.4); transition: 0.3s; 
}
.btn-save-lux:hover:not(:disabled) { transform: translateY(-2px) scale(1.02); box-shadow: 0 15px 25px -5px rgba(15, 23, 42, 0.5); }
.btn-save-lux:disabled { opacity: 0.7; cursor: not-allowed; }
.save-icon-glow { filter: drop-shadow(0 0 5px rgba(255,255,255,0.5)); }

.anim-fade-down { animation: fadeDown 0.6s cubic-bezier(0.16, 1, 0.3, 1); }
.anim-fade-up { animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) both; }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
@keyframes fadeDown { from { opacity: 0; transform: translateY(-30px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 1024px) {
  .hero-post-grid { grid-template-columns: 1fr; }
  .pricing-grid { grid-template-columns: 1fr; }
  .ads-layout { grid-template-columns: 1fr; }
  .card-body { padding: 24px; }
}
</style>