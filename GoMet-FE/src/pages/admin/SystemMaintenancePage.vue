<template>
  <div class="admin-page">
    <h2>Bảo trì hệ thống</h2>
    <p class="sub">Bật/tắt toàn hệ thống và soạn thông báo hiển thị cho người dùng ngay lập tức.</p>

    <div class="card">
      <label class="toggle-row">
        <span>Trạng thái</span>
        <div class="toggle-control">
          <span class="state-badge" :class="enabled ? 'on' : 'off'">
            {{ enabled ? 'BẬT' : 'TẮT' }}
          </span>
          <label class="switch" aria-label="Bật tắt bảo trì">
            <input type="checkbox" v-model="enabled" />
            <span class="slider"></span>
          </label>
        </div>
      </label>

      <p class="warn" v-if="enabled">Khi bật bảo trì: user/guest sẽ bị chuyển sang trang thông báo bảo trì.</p>
      <p class="warn off" v-else>Hệ thống đang ở trạng thái hoạt động bình thường.</p>

      <label class="label">Thông báo bảo trì</label>
      <textarea v-model="message" rows="5" maxlength="255" placeholder="Nhập thông báo hiển thị cho người dùng" />
      <div class="msg-tools">
        <small>{{ message.length }}/255</small>
        <div class="tool-actions">
          <button type="button" class="btn-ghost" @click="setDefaultMessage">Dùng mặc định</button>
          <button type="button" class="btn-ghost" @click="clearMessage">Xóa nội dung</button>
        </div>
      </div>

      <div class="preview">
        <p class="preview-title">Xem trước thông báo</p>
        <div class="preview-box">
          {{ previewMessage }}
        </div>
      </div>

      <div class="module-board">
        <div class="module-head">
          <p class="preview-title">Bảo trì theo chức năng</p>
          <small>{{ enabledModulesCount }}/{{ modules.length }} chức năng đang bật bảo trì</small>
        </div>
        <div class="module-list" v-if="modules.length">
          <div class="module-item" v-for="module in modules" :key="module.key">
            <div class="module-info">
              <strong>{{ module.name }}</strong>
              <small>{{ module.key }}</small>
            </div>
            <label class="switch" :aria-label="`Bật tắt bảo trì ${module.name}`">
              <input type="checkbox" v-model="module.enabled" />
              <span class="slider"></span>
            </label>
          </div>
        </div>
        <p v-else class="module-empty">Không có dữ liệu chức năng.</p>
      </div>

      <div class="actions">
        <button @click="save" :disabled="saving">{{ saving ? 'Đang lưu...' : 'Lưu cấu hình' }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useSystemSettingsStore } from '@/stores/systemSettings'
import { toast } from '@/composables/useToast'

const store = useSystemSettingsStore()
const enabled = ref(false)
const message = ref('')
const modules = ref([])
const saving = ref(false)
const DEFAULT_MESSAGE = 'Hệ thống đang bảo trì. Vui lòng quay lại sau.'

const previewMessage = computed(() =>
  message.value?.trim() || DEFAULT_MESSAGE
)

const enabledModulesCount = computed(() => modules.value.filter(m => m.enabled).length)

function normalizeModules (rawModules) {
  if (!Array.isArray(rawModules)) return []
  return rawModules.map(module => ({
    key: module?.key || '',
    name: module?.name || module?.key || 'Module',
    enabled: !!module?.enabled
  }))
}

async function loadData () {
  try {
    const data = await store.fetchAdminSettings()
    enabled.value = !!data.maintenanceMode
    message.value = data.message || ''
    modules.value = normalizeModules(data.modules)
  } catch (e) {
    toast.error('Không tải được cấu hình bảo trì')
  }
}

async function save () {
  saving.value = true
  try {
    await store.saveMaintenanceSettings({
      enabled: enabled.value,
      message: message.value,
      modules: modules.value.map(module => ({ key: module.key, enabled: !!module.enabled }))
    })
    toast.success('Cập nhật bảo trì thành công')
  } catch (e) {
    toast.error(e?.response?.data?.message || 'Không thể lưu cấu hình bảo trì')
  } finally {
    saving.value = false
  }
}

function setDefaultMessage () {
  message.value = DEFAULT_MESSAGE
}

function clearMessage () {
  message.value = ''
}

onMounted(loadData)
</script>

<style scoped>
.admin-page { padding: 24px; }
.sub {
  margin-top: -4px;
  color: #57534e;
  margin-bottom: 14px;
}
.card {
  max-width: 680px;
  background: #fff;
  border-radius: 12px;
  padding: 18px;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
}
.toggle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 700;
}
.toggle-control {
  display: flex;
  align-items: center;
  gap: 10px;
}
.state-badge {
  font-size: 12px;
  font-weight: 800;
  border-radius: 999px;
  padding: 4px 10px;
  letter-spacing: 0.3px;
}
.state-badge.on {
  background: #dcfce7;
  color: #166534;
}
.state-badge.off {
  background: #fee2e2;
  color: #991b1b;
}
.switch {
  position: relative;
  display: inline-block;
  width: 52px;
  height: 30px;
}
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}
.slider {
  position: absolute;
  cursor: pointer;
  inset: 0;
  background-color: #d6d3d1;
  transition: .2s;
  border-radius: 999px;
}
.slider:before {
  position: absolute;
  content: "";
  height: 22px;
  width: 22px;
  left: 4px;
  top: 4px;
  background-color: white;
  transition: .2s;
  border-radius: 999px;
}
.switch input:checked + .slider {
  background-color: #16a34a;
}
.switch input:checked + .slider:before {
  transform: translateX(22px);
}
.warn {
  margin-top: 0;
  margin-bottom: 12px;
  font-size: 14px;
  color: #92400e;
  background: #fef3c7;
  border: 1px solid #fde68a;
  border-radius: 10px;
  padding: 8px 10px;
}
.warn.off {
  color: #065f46;
  background: #ecfdf5;
  border-color: #a7f3d0;
}
.label { display: block; margin-bottom: 8px; font-weight: 600; }
textarea {
  width: 100%;
  border: 1px solid #d6d3d1;
  border-radius: 10px;
  padding: 10px;
  resize: vertical;
  box-sizing: border-box;
}
.msg-tools {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.tool-actions {
  display: flex;
  gap: 8px;
}
.btn-ghost {
  border: 1px solid #d6d3d1;
  border-radius: 8px;
  background: #fafaf9;
  color: #44403c;
  padding: 6px 10px;
  font-weight: 600;
  cursor: pointer;
}
.preview {
  margin-top: 14px;
}
.preview-title {
  margin: 0 0 8px;
  font-size: 13px;
  color: #78716c;
  font-weight: 700;
}
.preview-box {
  border: 1px dashed #cbd5e1;
  border-radius: 10px;
  background: #f8fafc;
  color: #334155;
  padding: 12px;
  min-height: 66px;
  line-height: 1.5;
}
.module-board {
  margin-top: 14px;
}
.module-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.module-head small {
  color: #78716c;
  font-weight: 600;
}
.module-list {
  border: 1px solid #e7e5e4;
  border-radius: 10px;
  background: #fafaf9;
  overflow: hidden;
}
.module-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 12px;
  border-bottom: 1px solid #e7e5e4;
}
.module-item:last-child {
  border-bottom: none;
}
.module-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.module-info strong {
  color: #1c1917;
}
.module-info small {
  color: #78716c;
  font-size: 12px;
}
.module-empty {
  margin: 0;
  padding: 10px;
  border-radius: 10px;
  background: #f5f5f4;
  color: #57534e;
}
.actions { margin-top: 14px; }
button {
  border: none;
  border-radius: 10px;
  background: #ea580c;
  color: #fff;
  padding: 10px 14px;
  font-weight: 700;
  cursor: pointer;
}
button:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
