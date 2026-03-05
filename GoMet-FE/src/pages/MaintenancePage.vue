<template>
  <div class="maintenance-wrap">
    <div class="orb orb-1"></div>
    <div class="orb orb-2"></div>

    <div class="maintenance-card">
      <div class="icon-wrap">🔧</div>
      <p class="eyebrow">GOMET SYSTEM NOTICE</p>
      <h1>Hệ thống đang bảo trì</h1>
      <p class="message">{{ maintenanceMessage }}</p>

      <div class="tips">
        <div class="tip-item">• Vui lòng thử lại sau vài phút.</div>
        <div class="tip-item">• Admin vẫn có thể truy cập khu vực quản trị.</div>
      </div>

      <router-link to="/" class="btn-home">Về trang chủ</router-link>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useSystemSettingsStore } from '@/stores/systemSettings'

const systemSettingsStore = useSystemSettingsStore()

const maintenanceMessage = computed(() =>
  systemSettingsStore.message || 'Hệ thống đang bảo trì. Vui lòng quay lại sau.'
)

onMounted(() => {
  systemSettingsStore.fetchPublicSettings().catch(() => {})
})
</script>

<style scoped>
.maintenance-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at 20% 20%, #44403c 0%, #1c1917 42%, #111827 100%);
  padding: 24px;
  position: relative;
  overflow: hidden;
}

.maintenance-card {
  max-width: 620px;
  width: 100%;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 20px;
  padding: 34px 30px;
  text-align: center;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.45);
  position: relative;
  z-index: 2;
}

.icon-wrap {
  margin: 0 auto 10px;
  width: 70px;
  height: 70px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  font-size: 30px;
  background: #ffedd5;
}

.eyebrow {
  margin: 0;
  font-size: 12px;
  letter-spacing: 1.2px;
  font-weight: 800;
  color: #9a3412;
}

.maintenance-card h1 {
  margin: 8px 0 12px;
  color: #1c1917;
  font-size: 34px;
  line-height: 1.2;
}

.message {
  color: #44403c;
  margin-bottom: 16px;
  line-height: 1.65;
  font-size: 16px;
}

.tips {
  text-align: left;
  background: #fff7ed;
  border: 1px solid #fed7aa;
  border-radius: 12px;
  padding: 10px 14px;
  margin-bottom: 20px;
}

.tip-item {
  color: #9a3412;
  font-size: 14px;
  line-height: 1.5;
}

.btn-home {
  display: inline-block;
  padding: 11px 20px;
  border-radius: 10px;
  text-decoration: none;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  font-weight: 700;
}

.orb {
  position: absolute;
  border-radius: 999px;
  filter: blur(28px);
  opacity: 0.35;
}

.orb-1 {
  width: 320px;
  height: 320px;
  background: #fb923c;
  top: -90px;
  left: -70px;
}

.orb-2 {
  width: 270px;
  height: 270px;
  background: #a855f7;
  bottom: -90px;
  right: -70px;
}
</style>
