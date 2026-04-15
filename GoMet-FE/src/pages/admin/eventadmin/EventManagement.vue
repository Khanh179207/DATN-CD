<template>
  <div class="gomet-admin-pro">
    <div class="header-zone">
      <div class="header-content">
        <h1 class="page-title">{{ t('admin.events.title') }}</h1>
        <p class="sub-text">{{ t('admin.events.subtitle') }}</p>
      </div>
      <button class="btn-create-mega" @click="openCreateModal">
        <span class="icon-plus">+</span> {{ t('admin.events.create') }}
      </button>
    </div>

    <div class="stats-bar">
      <div class="stat-item">
        <span class="s-label">{{ t('admin.events.total_events') }}</span><span class="s-val">{{ events?.length || 0 }}</span>
      </div>
      <div class="divider"></div>
      <div class="stat-item">
        <span class="s-label">{{ t('admin.events.status_active') }}</span><span class="s-val text-green">{{ ongoingCount }}</span>
      </div>
      <div class="divider"></div>
      <div class="stat-item">
        <span class="s-label">{{ t('admin.events.total_entries') }}</span><span class="s-val text-orange">{{ totalPostCount }}</span>
      </div>
    </div>

    <div class="toolbar">
      <div class="search-wrapper">
        <svg class="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
          stroke-width="2">
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
        <input v-model="searchQuery" type="text" :placeholder="t('admin.common.search_events')" />
      </div>
      <div class="filter-group">
        <select v-model="statusFilter">
          <option value="">{{ t('admin.events.all_statuses') }}</option>
          <option value="active">{{ t('admin.events.status_active') }}</option>
          <option value="upcoming">{{ t('admin.events.status_upcoming') }}</option>
          <option value="ended">{{ t('admin.events.status_ended') }}</option>
          <option value="deleted">{{ t('admin.events.status_hidden') }}</option>
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
      ⚠️ {{ error }} <button @click="loadEvents">{{ t('comment.retry_now') }}</button>
    </div>

    <div v-else class="event-list-container">
      <AdminEventCard v-for="ev in filteredEvents" :key="ev.eventID + '-' + (ev.isActive || 0) + '-' + (ev.isForceEnded || 0)
        " :ev="ev" @viewWinner="viewWinner" @openEditModal="openEditModal" @openViewModal="openViewModal"
        @deleteEvent="confirmDeleteRestore" @goToPostManagement="goToPostManagement" />

      <div v-if="!filteredEvents || filteredEvents.length === 0" class="empty-state">
        {{ t('admin.events.empty') }}
      </div>
    </div>

    <Transition name="zoom-in">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal-form wide-modal">
          <div class="mf-header">
            <div class="mf-header-left">
              <h3 v-if="isViewOnly">🔍 {{ t('admin.events.view_detail') }}</h3>
              <h3 v-else>
                {{ isEditing ? `✏️ ${t('admin.events.edit_event')}` : `✨ ${t('admin.events.create_event')}` }}
              </h3>
              <span v-if="isViewOnly" class="readonly-badge">{{ t('admin.events.read_only') }}</span>
            </div>
            <div class="mf-header-actions">
              <button class="btn-x" @click="showModal = false">✕</button>
            </div>
          </div>

          <div class="modal-scroll-area">
            <div class="edit-section-title">{{ t('admin.events.basic_info') }}</div>
            <div class="form-group">
              <label>{{ t('admin.events.event_name') }}
                <span v-if="!isViewOnly" class="req">*</span></label>
              <input v-model="form.eventName" type="text" :placeholder="t('admin.events.event_name_placeholder')"
                :disabled="isViewOnly" />
            </div>
            <div class="form-group">
              <label>{{ t('admin.events.description') }}</label>
              <textarea v-model="form.description" rows="3" :placeholder="t('admin.events.description_placeholder')"
                :disabled="isViewOnly"></textarea>
            </div>
            <div class="form-group">
              <label>{{ t('admin.events.banner') }}</label>
              <div class="upload-zone" :class="{ 'disabled-zone': isViewOnly }">
                <input v-if="!isViewOnly" type="file" id="fileUpload" accept="image/*" @change="handleFileUpload"
                  hidden />
                <div v-if="!imagePreview" class="upload-placeholder">
                  <label v-if="!isViewOnly" for="fileUpload" class="btn-upload">{{ t('admin.events.upload_image') }}</label>
                  <span v-else>{{ t('admin.events.no_image') }}</span>
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
                <h4 class="column-title">📜 {{ t('admin.events.rules_rewards') }}</h4>
                <div class="form-group">
                  <label>{{ t('admin.events.max_votes_per_user') }}
                    <span v-if="!isViewOnly" class="req">*</span></label>
                  <input v-model="form.maxVotes" type="number" min="1" :disabled="isViewOnly || isRulesLocked" />
                  <small v-if="isRulesLocked" style="color: #ea580c; font-size: 0.75rem; font-style: italic; margin-top: 6px; display: block;">
                    {{ t('admin.events.rules_locked_hint') }}
                  </small>
                </div>
                <div class="form-group">
                  <label>{{ t('admin.events.reward') }}</label>
                  <select v-model="form.rewardType" :disabled="isViewOnly || isRulesLocked"
                    style="width: 100%; padding: 12px 16px; border: 1px solid #dbe4ee; border-radius: 12px; font-size: 0.95rem; background: #ffffff; cursor: pointer; transition: all 0.28s ease;">
                    <option disabled value="">{{ t('admin.events.reward_type_placeholder') }}</option>
                    <option value="premium_1m">{{ t('admin.events.reward_premium_1m') }}</option>
                    <option value="premium_1y">{{ t('admin.events.reward_premium_1y') }}</option>
                    <option value="points">{{ t('admin.events.reward_points') }}</option>
                  </select>

                  <!-- PREMIUM 1 MONTH -->
                  <div v-if="form.rewardType === 'premium_1m'" class="form-group mt-3">
                    <div class="reward-info-box">
                      <i class="fas fa-info-circle"></i>
                      <p>{{ t('admin.events.reward_premium_1m_hint') }}</p>
                    </div>
                  </div>

                  <!-- PREMIUM 1 YEAR -->
                  <div v-if="form.rewardType === 'premium_1y'" class="form-group mt-3">
                    <div class="reward-info-box">
                      <i class="fas fa-info-circle"></i>
                      <p>{{ t('admin.events.reward_premium_1y_hint') }}</p>
                    </div>
                  </div>

                  <!-- POINTS -->
                  <div v-if="form.rewardType === 'points'" class="form-group mt-3">
                    <label>{{ t('admin.events.reward_points_label') }}</label>

                    <div class="grid grid-cols-3 gap-2">
                      <input v-model="form.pointsTop1" type="number" :placeholder="t('admin.events.top_1')" min="0"
                        :disabled="isViewOnly || isRulesLocked" />
                      <input v-model="form.pointsTop2" type="number" :placeholder="t('admin.events.top_2')" min="0"
                        :disabled="isViewOnly || isRulesLocked" />
                      <input v-model="form.pointsTop3" type="number" :placeholder="t('admin.events.top_3')" min="0"
                        :disabled="isViewOnly || isRulesLocked" />
                    </div>
                  </div>
                </div>
              </div>
              <div class="form-column">
                <h4 class="column-title">⏳ {{ t('admin.events.time_setup') }}</h4>
                <div class="form-group">
                  <label>{{ t('admin.events.submission_start') }}</label><input v-model="form.startAt" type="datetime-local"
                    :disabled="isViewOnly" />
                </div>
                <div class="form-group">
                  <label>{{ t('admin.events.submission_end') }}</label><input v-model="form.endAt" type="datetime-local"
                    :disabled="isViewOnly" />
                </div>
                <div class="form-group">
                  <label>{{ t('admin.events.voting_start') }}</label><input v-model="form.voteStartAt" type="datetime-local"
                    :disabled="isViewOnly" />
                </div>
                <div class="form-group">
                  <label>{{ t('admin.events.voting_end') }}</label><input v-model="form.voteEndAt" type="datetime-local"
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
                {{ t('admin.events.end_early') }}
              </button>
            </div>
            <div class="mf-actions-right">
              <button @click="showModal = false" class="btn-cancel">
                {{ isViewOnly ? t('admin.events.close') : t('admin.events.cancel_action') }}
              </button>
              <button v-if="!isViewOnly" @click="saveEvent" class="btn-save" :disabled="saving">
                <span v-if="saving">{{ t('admin.events.processing') }}</span><span v-else>{{
                  isEditing ? t('admin.events.save_changes') : t('admin.events.create_now')
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
          <h2>{{ t('admin.events.force_end_title') }}</h2>
          <p>
            {{ t('admin.events.force_end_message', { name: eventToClose?.eventName }) }}
          </p>
          <ul class="warning-list">
            <li>{{ t('admin.events.force_end_warning_entries') }}</li>
            <li>{{ t('admin.events.force_end_warning_votes') }}</li>
            <li>{{ t('admin.events.force_end_warning_winner') }}</li>
          </ul>
          <div class="confirm-actions">
            <button class="btn-gray" @click="showConfirmEndModal = false">
              {{ t('admin.events.cancel_action') }}
            </button>
            <button class="btn-danger" @click="executeForceEnd">
              {{ t('admin.events.force_end_anyway') }}
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
              isActionSoftDelete ? t('admin.events.confirm_hide_title') : t('admin.events.confirm_restore_title')
            }}
          </h2>
          <p v-if="isActionSoftDelete">
            {{ t('admin.events.hide_confirm', { name: eventTargetName }) }}
          </p>
          <p v-else>
            {{ t('admin.events.restore_confirm', { name: eventTargetName }) }}
          </p>

          <div class="confirm-actions mt-4">
            <button class="btn-gray" @click="showDeleteRestoreModal = false">
              {{ t('admin.events.cancel_action') }}
            </button>
            <button :class="isActionSoftDelete ? 'btn-danger' : 'btn-success'" @click="executeDeleteRestore">
              {{ isActionSoftDelete ? t('admin.events.hide_anyway') : t('admin.events.restore_now') }}
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <Transition name="zoom-in">
      <div v-if="showWinnerModal" class="modal-overlay" @click.self="showWinnerModal = false">
        <div class="winner-modal-card">
          <div class="winner-header">
            <h3>👑 {{ t('admin.events.winner_title') }}</h3>
            <button class="btn-x" @click="showWinnerModal = false">✕</button>
          </div>
          <div v-if="loadingWinner" class="winner-loading">
            {{ t('admin.events.calculating_results') }}
          </div>
          <div v-else-if="!winnerData" class="winner-empty">
            {{ t('admin.events.no_valid_entry') }}
          </div>
          <div v-else class="winner-content">
            <div class="winner-medal">🥇</div>
            <img :src="getImageUrl(winnerData.postImage)" :alt="t('admin.events.winning_recipe_alt')" class="winner-recipe-img" />
            <h2 class="winner-title">{{ winnerData.postTitle }}</h2>
            <p class="winner-author">
              {{ t('admin.events.author') }} <strong>{{ winnerData.username }}</strong>
            </p>
            <p class="winner-id">
              {{ t('admin.events.account_id') }}: <strong>#{{ winnerData.accountID }}</strong>
            </p>
            <div class="winner-votes">
              <span class="v-count">{{ winnerData.voteCount }}</span>
              <span class="v-lbl">{{ t('admin.events.votes') }}</span>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import api from "@/services/api.js";
import { toast } from "@/composables/useToast";
import { uploadMedia } from "@/services/uploadService";
import AdminEventCard from "./AdminEventCard.vue";
import "./EventManagement.scss";

const router = useRouter();
const { t } = useI18n();

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
  () => {
    form.pointsTop1 = null;
    form.pointsTop2 = null;
    form.pointsTop3 = null;
  }
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
const parseRewardForForm = (rewardStr) => {
  const result = { rewardType: "", pointsTop1: null, pointsTop2: null, pointsTop3: null };
  if (!rewardStr) return result;
  
  const parts = rewardStr.split("|");
  const type = parts[0];
  
  if (type === "PREMIUM_1M") {
    result.rewardType = "premium_1m";
  } else if (type === "PREMIUM_1Y") {
    result.rewardType = "premium_1y";
  } else if (type === "POINTS") {
    result.rewardType = "points";
    result.pointsTop1 = Number(parts[1]) || null;
    result.pointsTop2 = Number(parts[2]) || null;
    result.pointsTop3 = Number(parts[3]) || null;
  }
  return result;
};

// --- LOGIC KHÓA LUẬT & THƯỞNG ---
const isRulesLocked = computed(() => {
  if (!isEditing.value) return false;
  const status = getStatusHelper(form);
  return status === 'active' || status === 'ended' || form.isForceEnded === 1;
});

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
      const sorted = res.data.sort((a, b) => b.voteCount - a.voteCount);
      // Lưu top 3
      event.topWinners = sorted.slice(0, 3);
      event.winnerData = sorted[0]; // Top 1 cho backward compatibility
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
    error.value = t("admin.events.load_failed");
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
  
  // Dịch chuỗi phần thưởng từ DB ra
  const parsedReward = parseRewardForForm(ev.reward);

  Object.assign(form, {
    ...ev,
    startAt: formatForInput(ev.startAt),
    endAt: formatForInput(ev.endAt),
    voteStartAt: formatForInput(ev.voteStartAt),
    voteEndAt: formatForInput(ev.voteEndAt),
    originalEndAt: ev.originalEndAt || ev.endAt,
    originalVoteEndAt: ev.originalVoteEndAt || ev.voteEndAt,
    // Đổ dữ liệu vào Form
    rewardType: parsedReward.rewardType,
    pointsTop1: parsedReward.pointsTop1,
    pointsTop2: parsedReward.pointsTop2,
    pointsTop3: parsedReward.pointsTop3,
  });
  imagePreview.value = getImageUrl(ev.bannerImage);
  showModal.value = true;
};

const openViewModal = (ev) => {
  isEditing.value = false;
  isViewOnly.value = true;
  removeImage();
  
  // Dịch chuỗi phần thưởng từ DB ra
  const parsedReward = parseRewardForForm(ev.reward);

  Object.assign(form, {
    ...ev,
    startAt: formatForInput(ev.startAt),
    endAt: formatForInput(ev.endAt),
    voteStartAt: formatForInput(ev.voteStartAt),
    voteEndAt: formatForInput(ev.voteEndAt),
    // Đổ dữ liệu vào Form
    rewardType: parsedReward.rewardType,
    pointsTop1: parsedReward.pointsTop1,
    pointsTop2: parsedReward.pointsTop2,
    pointsTop3: parsedReward.pointsTop3,
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

    toast.success(t("admin.events.force_end_ok"));
    showConfirmEndModal.value = false;
    eventToClose.value = null;

    // Delay 1s để hệ thống xử lý xong
    setTimeout(() => loadEvents(), 1000);
  } catch (e) {
    toast.error(t("admin.events.force_end_fail"));
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
    toast.error(t("admin.events.winner_load_fail"));
  } finally {
    loadingWinner.value = false;
  }
};

const saveEvent = async () => {
  if (isViewOnly.value) return;
  if (!form.eventName.trim()) return toast.warn(t("admin.events.name_required"));

  if (!form.startAt || !form.endAt || !form.voteStartAt || !form.voteEndAt) {
    return toast.warn(t("admin.events.time_required"));
  }

  const dStart = new Date(form.startAt);
  const dEnd = new Date(form.endAt);
  const dVoteStart = new Date(form.voteStartAt);
  const dVoteEnd = new Date(form.voteEndAt);

  if (dEnd <= dStart) return toast.warn(t("admin.events.submit_end_after_start"));
  if (dVoteEnd <= dVoteStart) return toast.warn(t("admin.events.vote_end_after_start"));
  if (dVoteStart < dStart) return toast.warn(t("admin.events.vote_start_after_submit_start"));
  if (dVoteEnd < dEnd) return toast.warn(t("admin.events.vote_end_after_submit_end"));

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

    if (form.rewardType === "premium_1m") {
      rewardText = "PREMIUM_1M|30|30|30";
    } else if (form.rewardType === "premium_1y") {
      rewardText = "PREMIUM_1Y|365|365|365";
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

    toast.success(t("admin.events.save_ok"));
    loadEvents();
    showModal.value = false;
  } catch (e) {
    toast.error(t("admin.events.server_error"));
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

    toast.success(isSoftDelete ? t("admin.events.hide_ok") : t("admin.events.restore_ok"));
    showDeleteRestoreModal.value = false;

    loadEvents();
  } catch (e) {
    toast.error(t("admin.events.action_error"));
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
      toast.success(t("admin.events.reward_ok"));
      loadEvents();
    } else {
      toast.warn(res.data?.message || t("admin.events.reward_unavailable"));
    }
  } catch (error) {
    toast.error(error.response?.data?.message || t("admin.events.reward_fail"));
    console.error(error);
  }
};
</script>
