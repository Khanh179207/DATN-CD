<template>
  <div class="gomet-admin-pro">
    <div class="header-zone">
      <div class="header-content">
        <h1 class="page-title">Quản lý Sự kiện</h1>
        <p class="sub-text">Tối ưu trải nghiệm cuộc thi ẩm thực của GOMET</p>
      </div>
      <button class="btn-create-mega" @click="openCreateModal">
        <span class="icon-plus">+</span> Tạo mới sự kiện
      </button>
    </div>

    <div class="stats-bar">
      <div class="stat-item">
        <span class="s-label">Tổng sự kiện</span><span class="s-val">{{ events?.length || 0 }}</span>
      </div>
      <div class="divider"></div>
      <div class="stat-item">
        <span class="s-label">Đang diễn ra</span><span class="s-val text-green">{{ ongoingCount }}</span>
      </div>
      <div class="divider"></div>
      <div class="stat-item">
        <span class="s-label">Tổng bài thi</span><span class="s-val text-orange">{{ totalPostCount }}</span>
      </div>
    </div>

    <div class="toolbar">
      <div class="search-wrapper">
        <svg class="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
          stroke-width="2">
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
        <input v-model="searchQuery" type="text" placeholder="Tìm kiếm tên sự kiện..." />
      </div>
      <div class="filter-group">
        <select v-model="statusFilter">
          <option value="">Tất cả trạng thái</option>
          <option value="active">Đang diễn ra</option>
          <option value="upcoming">Sắp diễn ra</option>
          <option value="ended">Đã kết thúc</option>
          <option value="deleted">Đã bị ẩn (Xóa)</option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="event-list-container">
      <div v-for="n in 3" :key="n" class="event-row-card skeleton">
        <div class="skel-img"></div>
        <div class="skel-body">
          <div class="skel-line"></div>
          <div class="skel-line short"></div>
        </div>
      </div>
    </div>

    <div v-else-if="error" class="error-banner">
      ⚠️ {{ error }} <button @click="loadEvents">Thử lại</button>
    </div>

    <div v-else class="event-list-container">
      <AdminEventCard v-for="ev in filteredEvents" :key="ev.eventID + '-' + (ev.isActive || 0) + '-' + (ev.isForceEnded || 0)
        " :ev="ev" @viewWinner="viewWinner" @openEditModal="openEditModal" @openViewModal="openViewModal"
        @deleteEvent="confirmDeleteRestore" @goToPostManagement="goToPostManagement" />

      <div v-if="!filteredEvents || filteredEvents.length === 0" class="empty-state">
        Không tìm thấy sự kiện nào.
      </div>
    </div>

    <Transition name="zoom-in">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal-form wide-modal">
          <div class="mf-header">
            <div class="mf-header-left">
              <h3 v-if="isViewOnly">🔍 Xem chi tiết Sự kiện</h3>
              <h3 v-else>
                {{ isEditing ? "✏️ Chỉnh sửa sự kiện" : "✨ Tạo sự kiện mới" }}
              </h3>
              <span v-if="isViewOnly" class="readonly-badge">Chỉ đọc</span>
            </div>
            <div class="mf-header-actions">
              <button class="btn-x" @click="showModal = false">✕</button>
            </div>
          </div>

          <div class="modal-scroll-area">
            <div class="edit-section-title">Thông tin cơ bản</div>
            <div class="form-group">
              <label>Tên sự kiện
                <span v-if="!isViewOnly" class="req">*</span></label>
              <input v-model="form.eventName" type="text" placeholder="VD: Top Chef Gomet 2026"
                :disabled="isViewOnly" />
            </div>
            <div class="form-group">
              <label>Mô tả sự kiện</label>
              <textarea v-model="form.description" rows="3" placeholder="Giới thiệu về cuộc thi..."
                :disabled="isViewOnly"></textarea>
            </div>
            <div class="form-group">
              <label>Ảnh Banner</label>
              <div class="upload-zone" :class="{ 'disabled-zone': isViewOnly }">
                <input v-if="!isViewOnly" type="file" id="fileUpload" accept="image/*" @change="handleFileUpload"
                  hidden />
                <div v-if="!imagePreview" class="upload-placeholder">
                  <label v-if="!isViewOnly" for="fileUpload" class="btn-upload">Tải ảnh lên</label>
                  <span v-else>Không có ảnh</span>
                </div>
                <div v-else class="preview-container">
                  <img :src="imagePreview" class="preview-img" />
                  <button v-if="!isViewOnly" class="btn-remove-img" @click="removeImage">
                    ✕
                  </button>
                </div>
              </div>
            </div>

            <div class="form-grid">
              <div class="form-column">
                <h4 class="column-title">📜 Quy tắc</h4>
                <div class="form-group">
                  <label>Số phiếu Vote/User
                    <span v-if="!isViewOnly" class="req">*</span></label>
                  <input v-model="form.maxVotes" type="number" min="1" :disabled="isViewOnly" />
                </div>
                <div class="form-group">
                  <label>Phần thưởng vinh danh</label>
                  <select v-model="form.rewardType" :disabled="isViewOnly"
                    style="width: 100%; padding: 12px 16px; border: 1px solid #dbe4ee; border-radius: 12px; font-size: 0.95rem; background: #ffffff; cursor: pointer; transition: all 0.28s ease;">
                    <option disabled value="">Chọn loại phần thưởng</option>
                    <option value="premium">Premium</option>
                    <option value="points">Points</option>
                  </select>

                  <!-- PREMIUM -->
                  <div v-if="form.rewardType === 'premium'" class="form-group mt-3">
                    <label>Thời gian Premium cho Top 1 (ngày)</label>
                    <input v-model.number="form.premiumDaysTop1" type="number" min="1" placeholder="VD: 30"
                      :disabled="isViewOnly" class="mb-2" />
                    <label>Thời gian Premium cho Top 2 (ngày)</label>
                    <input v-model.number="form.premiumDaysTop2" type="number" min="1" placeholder="VD: 15"
                      :disabled="isViewOnly" class="mb-2" />
                    <label>Thời gian Premium cho Top 3 (ngày)</label>
                    <input v-model.number="form.premiumDaysTop3" type="number" min="1" placeholder="VD: 7"
                      :disabled="isViewOnly" />
                  </div>

                  <!-- POINTS -->
                  <div v-if="form.rewardType === 'points'" class="form-group mt-3">
                    <label>Phần thưởng Points</label>

                    <div class="grid grid-cols-3 gap-2">
                      <input v-model="form.pointsTop1" type="number" placeholder="Top 1" min="0"
                        :disabled="isViewOnly" />
                      <input v-model="form.pointsTop2" type="number" placeholder="Top 2" min="0"
                        :disabled="isViewOnly" />
                      <input v-model="form.pointsTop3" type="number" placeholder="Top 3" min="0"
                        :disabled="isViewOnly" />
                    </div>
                  </div>
                </div>
              </div>
              <div class="form-column">
                <h4 class="column-title">⏳ Thiết lập thời gian</h4>
                <div class="form-group">
                  <label>Bắt đầu nhận bài</label><input v-model="form.startAt" type="datetime-local"
                    :disabled="isViewOnly" />
                </div>
                <div class="form-group">
                  <label>Kết thúc nhận bài</label><input v-model="form.endAt" type="datetime-local"
                    :disabled="isViewOnly" />
                </div>
                <div class="form-group">
                  <label>Bắt đầu bình chọn</label><input v-model="form.voteStartAt" type="datetime-local"
                    :disabled="isViewOnly" />
                </div>
                <div class="form-group">
                  <label>Kết thúc bình chọn</label><input v-model="form.voteEndAt" type="datetime-local"
                    :disabled="isViewOnly" />
                </div>
              </div>
            </div>
          </div>

          <div class="mf-actions">
            <div class="mf-actions-left">
              <button v-if="
                isEditing &&
                !isViewOnly &&
                (getStatusHelper(form) === 'active' ||
                  getStatusHelper(form) === 'upcoming')
              " class="btn-end-event" @click="confirmForceEnd(form)">
                Kết thúc sớm
              </button>
            </div>
            <div class="mf-actions-right">
              <button @click="showModal = false" class="btn-cancel">
                {{ isViewOnly ? "Đóng" : "Hủy" }}
              </button>
              <button v-if="!isViewOnly" @click="saveEvent" class="btn-save" :disabled="saving">
                <span v-if="saving">Đang xử lý...</span><span v-else>{{
                  isEditing ? "Lưu thay đổi" : "Tạo ngay"
                }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <Transition name="zoom-in">
      <div v-if="showConfirmEndModal" class="modal-overlay" @click.self="showConfirmEndModal = false">
        <div class="confirm-modal">
          <div class="icon-warning">
            <i class="fas fa-exclamation-triangle"></i>
          </div>
          <h2>Xác nhận kết thúc sớm</h2>
          <p>
            Bạn đang yêu cầu đóng sự kiện
            <strong>"{{ eventToClose?.eventName }}"</strong> ngay lập tức.
          </p>
          <ul class="warning-list">
            <li>Người dùng sẽ không thể nộp thêm bài dự thi.</li>
            <li>Chức năng bình chọn sẽ bị khóa.</li>
            <li>Hệ thống sẽ ngay lập tức tính toán người chiến thắng.</li>
          </ul>
          <div class="confirm-actions">
            <button class="btn-gray" @click="showConfirmEndModal = false">
              Hủy thao tác
            </button>
            <button class="btn-danger" @click="executeForceEnd">
              Vẫn đóng sự kiện
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <Transition name="zoom-in">
      <div v-if="showDeleteRestoreModal" class="modal-overlay" @click.self="showDeleteRestoreModal = false">
        <div class="confirm-modal">
          <div class="icon-warning" :style="{ color: isActionSoftDelete ? '#ef4444' : '#10b981' }">
            <i :class="isActionSoftDelete ? 'fas fa-trash-alt' : 'fas fa-redo'"></i>
          </div>

          <h2>
            {{
              isActionSoftDelete ? "Xác nhận ẩn sự kiện" : "Xác nhận khôi phục"
            }}
          </h2>
          <p v-if="isActionSoftDelete">
            Bạn có chắc muốn ẩn sự kiện
            <strong>"{{ eventTargetName }}"</strong> khỏi hệ thống?
          </p>
          <p v-else>
            Sự kiện <strong>"{{ eventTargetName }}"</strong> sẽ được hiển thị
            trở lại cho người dùng. Bạn đồng ý chứ?
          </p>

          <div class="confirm-actions mt-4">
            <button class="btn-gray" @click="showDeleteRestoreModal = false">
              Hủy bỏ
            </button>
            <button :class="isActionSoftDelete ? 'btn-danger' : 'btn-success'" @click="executeDeleteRestore">
              {{ isActionSoftDelete ? "Vẫn Ẩn Sự Kiện" : "Khôi Phục Ngay" }}
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <Transition name="zoom-in">
      <div v-if="showWinnerModal" class="modal-overlay" @click.self="showWinnerModal = false">
        <div class="winner-modal-card">
          <div class="winner-header">
            <h3>👑 Người chiến thắng</h3>
            <button class="btn-x" @click="showWinnerModal = false">✕</button>
          </div>
          <div v-if="loadingWinner" class="winner-loading">
            Đang tính kết quả...
          </div>
          <div v-else-if="!winnerData" class="winner-empty">
            Không tìm thấy bài dự thi hợp lệ.
          </div>
          <div v-else class="winner-content">
            <div class="winner-medal">🥇</div>
            <img :src="getImageUrl(winnerData.postImage)" alt="Winning Recipe" class="winner-recipe-img" />
            <h2 class="winner-title">{{ winnerData.postTitle }}</h2>
            <p class="winner-author">
              Tác giả: <strong>{{ winnerData.username }}</strong>
            </p>
            <p class="winner-id">
              Account ID: <strong>#{{ winnerData.accountID }}</strong>
            </p>
            <div class="winner-votes">
              <span class="v-count">{{ winnerData.voteCount }}</span>
              <span class="v-lbl">Lượt vote</span>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import api from "@/services/api.js";
import { toast } from "@/composables/useToast";
import { uploadMedia } from "@/services/uploadService";
import AdminEventCard from "./AdminEventCard.vue";
import "./EventManagement.scss";

const router = useRouter();

const events = ref([]);
const loading = ref(true);
const error = ref(null);
const searchQuery = ref("");
const statusFilter = ref("");

const showModal = ref(false);
const isEditing = ref(false);
const isViewOnly = ref(false);
const saving = ref(false);
const imagePreview = ref(null);

const showWinnerModal = ref(false);
const winnerData = ref(null);
const loadingWinner = ref(false);

const showConfirmEndModal = ref(false);
const eventToClose = ref(null);

// State cho Modal Delete/Restore
const showDeleteRestoreModal = ref(false);
const eventTargetId = ref(null);
const eventTargetName = ref("");
const isActionSoftDelete = ref(true);

const form = reactive({
  eventID: null,
  eventName: "",
  bannerFile: null,
  bannerImage: "",
  description: "",
  rules: "",
  reward: "",
  rewardType: "",
  premiumDaysTop1: null,
  premiumDaysTop2: null,
  premiumDaysTop3: null,
  pointsTop1: null,
  pointsTop2: null,
  pointsTop3: null,
  maxVotes: 3,
  startAt: "",
  endAt: "",
  voteStartAt: "",
  voteEndAt: "",
  originalEndAt: "",
  originalVoteEndAt: "",
  winner: null,
  isActive: 1,
  isForceEnded: 0,
});

watch(
  () => form.rewardType,
  (val) => {
    form.premiumDaysTop1 = null;
    form.premiumDaysTop2 = null;
    form.premiumDaysTop3 = null;
    form.pointsTop1 = null;
    form.pointsTop2 = null;
    form.pointsTop3 = null;
  },
);

// --- Hàm hỗ trợ ---
const getImageUrl = (path) => {
  if (!path)
    return "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=300";
  return path.startsWith("/uploads")
    ? `http://localhost:8080${encodeURI(path)}`
    : path;
};

const getStatusHelper = (ev) => {
  if (!ev) return "upcoming";
  if (Number(ev.isActive) === 0) return "deleted";
  if (Number(ev.isForceEnded) === 1) return "ended";
  if (!ev.startAt || !ev.voteEndAt) return "upcoming";

  const now = new Date();
  if (now < new Date(ev.startAt)) return "upcoming";
  if (now > new Date(ev.voteEndAt)) return "ended";
  return "active";
};

const formatForInput = (dateStr) => {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return ""; // 🔥 Bổ sung kiểm tra Date hợp lệ
  const z = (n) => (n < 10 ? "0" : "") + n;
  return `${d.getFullYear()}-${z(d.getMonth() + 1)}-${z(d.getDate())}T${z(d.getHours())}:${z(d.getMinutes())}`;
};

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    form.bannerFile = file;
    imagePreview.value = URL.createObjectURL(file);
  }
};

const removeImage = () => {
  form.bannerFile = null;
  form.bannerImage = "";
  imagePreview.value = null;
};

// --- Tương tác API ---
const fetchWinnerForEvent = async (event) => {
  if (!event || !event.eventID) return;
  try {
    const res = await api.get(`/api/admin/events/${event.eventID}/posts`);
    if (res.data?.length > 0) {
      event.winnerData = res.data.sort((a, b) => b.voteCount - a.voteCount)[0];
      if (!event.winner || event.winner !== event.winnerData.accountID) {
        await api.put(`/api/admin/events/${event.eventID}`, {
          ...event,
          winner: event.winnerData.accountID,
        });
      }
    }
  } catch (error) {
    console.error("Lỗi lấy Winner", error);
  }
};

const loadEvents = async () => {
  loading.value = true;
  error.value = null;
  try {
    const res = await api.get("/api/admin/events");

    // 🔥 ĐỔ BÊ TÔNG BẢO VỆ DỮ LIỆU CHỐNG CRASH TRANG
    const rawData = res.data?.data || res.data;
    events.value = Array.isArray(rawData) ? rawData : [];

    events.value.forEach((ev) => {
      if (getStatusHelper(ev) === "ended" && !ev.winnerData) {
        fetchWinnerForEvent(ev);
      }
    });
  } catch (e) {
    error.value = "Tải dữ liệu thất bại. Vui lòng kiểm tra kết nối mạng!";
    events.value = []; // Gán mảng rỗng để không bị văng code
  } finally {
    loading.value = false;
  }
};

onMounted(loadEvents);

// 🔥 Bảo vệ mảng an toàn trong các Computed
const ongoingCount = computed(
  () =>
    (Array.isArray(events.value) ? events.value : []).filter(
      (e) => getStatusHelper(e) === "active",
    ).length,
);
const upcomingCount = computed(
  () =>
    (Array.isArray(events.value) ? events.value : []).filter(
      (e) => getStatusHelper(e) === "upcoming",
    ).length,
);
const totalPostCount = computed(() =>
  (Array.isArray(events.value) ? events.value : [])
    .filter((e) => getStatusHelper(e) !== "deleted")
    .reduce((sum, ev) => sum + (Number(ev.postCount) || 0), 0),
);

const filteredEvents = computed(() => {
  if (!Array.isArray(events.value)) return [];
  return events.value.filter((ev) => {
    const matchSearch =
      !searchQuery.value ||
      (ev.eventName || "")
        .toLowerCase()
        .includes(searchQuery.value.toLowerCase());
    const matchStatus =
      !statusFilter.value || getStatusHelper(ev) === statusFilter.value;
    return matchSearch && matchStatus;
  });
});

// --- Hành động Modals ---
const openCreateModal = () => {
  isEditing.value = false;
  isViewOnly.value = false;
  removeImage();
  Object.assign(form, {
    eventID: null,
    eventName: "",
    bannerFile: null,
    bannerImage: "",
    description: "",
    rules: "",
    reward: "",
    rewardType: "",
    premiumDaysTop1: null,
    premiumDaysTop2: null,
    premiumDaysTop3: null,
    pointsTop1: null,
    pointsTop2: null,
    pointsTop3: null,
    maxVotes: 3,
    startAt: "",
    endAt: "",
    voteStartAt: "",
    voteEndAt: "",
    originalEndAt: "",
    originalVoteEndAt: "",
    winner: null,
    isActive: 1,
    isForceEnded: 0,
  });
  showModal.value = true;
};

const openEditModal = (ev) => {
  isEditing.value = true;
  isViewOnly.value = false;
  removeImage();
  Object.assign(form, {
    ...ev,
    startAt: formatForInput(ev.startAt),
    endAt: formatForInput(ev.endAt),
    voteStartAt: formatForInput(ev.voteStartAt),
    voteEndAt: formatForInput(ev.voteEndAt),
    originalEndAt: ev.originalEndAt || ev.endAt,
    originalVoteEndAt: ev.originalVoteEndAt || ev.voteEndAt,
  });
  imagePreview.value = getImageUrl(ev.bannerImage);
  showModal.value = true;
};

const openViewModal = (ev) => {
  isEditing.value = false;
  isViewOnly.value = true;
  removeImage();
  Object.assign(form, {
    ...ev,
    startAt: formatForInput(ev.startAt),
    endAt: formatForInput(ev.endAt),
    voteStartAt: formatForInput(ev.voteStartAt),
    voteEndAt: formatForInput(ev.voteEndAt),
  });
  imagePreview.value = getImageUrl(ev.bannerImage);
  showModal.value = true;
};

const confirmForceEnd = (ev) => {
  eventToClose.value = ev;
  showConfirmEndModal.value = true;
};

const executeForceEnd = async () => {
  if (!eventToClose.value) return;
  const ev = eventToClose.value;
  try {
    // 1️⃣ Gọi API force-end event (sẽ tự động thưởng)
    await api.post(`/api/admin/events/${ev.eventID}/force-end`);

    toast.success("Sự kiện đã được đóng khẩn cấp! Đang tính kết quả... ⚡");
    showConfirmEndModal.value = false;
    eventToClose.value = null;

    // Delay 1s để hệ thống xử lý xong
    setTimeout(() => loadEvents(), 1000);
  } catch (e) {
    toast.error("Lỗi khi kết thúc sự kiện!");
    console.error(e);
  }
};

const viewWinner = async (ev) => {
  showWinnerModal.value = true;
  loadingWinner.value = true;
  winnerData.value = null;
  try {
    if (ev.winnerData) {
      winnerData.value = ev.winnerData;
    } else {
      const res = await api.get(`/api/admin/events/${ev.eventID}/posts`);
      if (res.data?.length > 0)
        winnerData.value = res.data.sort(
          (a, b) => b.voteCount - a.voteCount,
        )[0];
    }
  } catch (e) {
    toast.error("Lỗi lấy dữ liệu");
  } finally {
    loadingWinner.value = false;
  }
};

const saveEvent = async () => {
  if (isViewOnly.value) return;
  if (!form.eventName.trim()) return toast.warn("Vui lòng nhập tên sự kiện");
  saving.value = true;
  try {
    let finalBannerUrl = form.bannerImage;
    if (form.bannerFile)
      finalBannerUrl = await uploadMedia(form.bannerFile, "events");

    let finalEndAt = form.endAt;
    let finalVoteEndAt = form.voteEndAt;

    if (
      form.isForceEnded === 0 &&
      form.originalEndAt &&
      new Date(form.originalEndAt) > new Date(form.endAt)
    ) {
      finalEndAt = form.originalEndAt;
      finalVoteEndAt = form.originalVoteEndAt;
    }

    let rewardText = "";

    if (form.rewardType === "premium") {
      rewardText = `PREMIUM|${form.premiumDaysTop1 || 0}|${form.premiumDaysTop2 || 0}|${form.premiumDaysTop3 || 0}`;
    } else if (form.rewardType === "points") {
      rewardText = `POINTS|${form.pointsTop1 || 0}|${form.pointsTop2 || 0}|${form.pointsTop3 || 0}`;
    }
    const eventData = {
      ...form,
      reward: rewardText,
      bannerImage: finalBannerUrl,
      endAt: finalEndAt,
      voteEndAt: finalVoteEndAt,
    };

    if (isEditing.value)
      await api.put(`/api/admin/events/${form.eventID}`, eventData);
    else await api.post("/api/admin/events", eventData);

    toast.success("Lưu thành công! 🚀");
    loadEvents();
    showModal.value = false;
  } catch (e) {
    toast.error("Lỗi máy chủ!");
  } finally {
    saving.value = false;
  }
};

const confirmDeleteRestore = (id, isSoftDelete) => {
  if (!Array.isArray(events.value)) return;
  const ev = events.value.find((e) => e.eventID === id);
  if (!ev) return;

  eventTargetId.value = id;
  eventTargetName.value = ev.eventName;
  isActionSoftDelete.value = isSoftDelete;
  showDeleteRestoreModal.value = true;
};

const executeDeleteRestore = async () => {
  const id = eventTargetId.value;
  const isSoftDelete = isActionSoftDelete.value;

  try {
    if (isSoftDelete) {
      await api.delete(`/api/admin/events/${id}`);
      const index = events.value.findIndex((e) => e.eventID === id);
      if (index !== -1) events.value[index].isActive = 0;
    } else {
      await api.put(`/api/admin/events/${id}/restore`);
      const index = events.value.findIndex((e) => e.eventID === id);
      if (index !== -1) events.value[index].isActive = 1;
    }

    toast.success(
      isSoftDelete ? "Đã ẩn sự kiện thành công!" : "Đã khôi phục sự kiện!",
    );
    showDeleteRestoreModal.value = false;

    loadEvents();
  } catch (e) {
    toast.error("Lỗi thao tác trên hệ thống!");
    console.error(e);
  }
};

const goToPostManagement = (eventID) => {
  router.push({ name: "AdminPostEventManagement", params: { id: eventID } });
};

// 🎁 REWARD FUNCTIONS
const rewardTopUsers = async (eventID) => {
  try {
    const res = await api.post(`/api/admin/events/${eventID}/reward`);
    if (res.data?.success) {
      toast.success("Thưởng người thắng thành công! 🎉");
      loadEvents();
    } else {
      toast.warn(res.data?.message || "Không thể thưởng sự kiện này!");
    }
  } catch (error) {
    toast.error(error.response?.data?.message || "Lỗi khi thưởng người thắng!");
    console.error(error);
  }
};
</script>
