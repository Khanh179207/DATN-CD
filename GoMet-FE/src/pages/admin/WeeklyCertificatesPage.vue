<template>
  <div class="admin-page">
    <h2>Top tuần & Cấp bằng</h2>

    <div class="toolbar card">
      <label>
        Từ ngày
        <input type="date" v-model="weekStart" />
      </label>
      <label>
        Đến ngày
        <input type="date" v-model="weekEnd" />
      </label>
      <label>
        Top
        <input type="number" v-model.number="top" min="1" max="20" />
      </label>
      <button @click="loadAll" :disabled="loading">Tải dữ liệu</button>
      <button @click="generate" :disabled="loading">Generate Certificates</button>
    </div>

    <div class="card">
      <h3>Leaderboard tuần</h3>
      <table>
        <thead>
          <tr><th>Rank</th><th>User</th><th>Score</th></tr>
        </thead>
        <tbody>
          <tr v-for="row in leaderboard" :key="`${row.userId}-${row.rank}`">
            <td>#{{ row.rank }}</td>
            <td>{{ row.username }}</td>
            <td>{{ row.score }}</td>
          </tr>
          <tr v-if="!leaderboard.length"><td colspan="3">Không có dữ liệu</td></tr>
        </tbody>
      </table>
    </div>

    <div class="card">
      <h3>Certificates đã cấp</h3>
      <table>
        <thead>
          <tr><th>Tuần</th><th>User</th><th>Rank</th><th>Code</th><th>Issued</th></tr>
        </thead>
        <tbody>
          <tr v-for="cert in certificates" :key="cert.id">
            <td>{{ cert.weekStart }} → {{ cert.weekEnd }}</td>
            <td>{{ cert.username }}</td>
            <td>#{{ cert.rank }}</td>
            <td>{{ cert.certificateCode }}</td>
            <td>{{ formatDate(cert.issuedAt) }}</td>
          </tr>
          <tr v-if="!certificates.length"><td colspan="5">Chưa có certificates</td></tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { toast } from '@/composables/useToast'
import {
  getWeeklyLeaderboard,
  getWeeklyCertificates,
  generateWeeklyCertificates
} from '@/services/adminCertificateService'

const leaderboard = ref([])
const certificates = ref([])
const loading = ref(false)

const now = new Date()
const currentMonday = new Date(now)
const day = currentMonday.getDay()
const offset = day === 0 ? 6 : day - 1
currentMonday.setDate(currentMonday.getDate() - offset)
const sunday = new Date(currentMonday)
sunday.setDate(currentMonday.getDate() + 6)

const weekStart = ref(currentMonday.toISOString().slice(0, 10))
const weekEnd = ref(sunday.toISOString().slice(0, 10))
const top = ref(10)

const formatDate = (value) => value ? new Date(value).toLocaleString('vi-VN') : '-'

async function loadAll () {
  loading.value = true
  try {
    const [lb, cert] = await Promise.all([
      getWeeklyLeaderboard({ weekStart: weekStart.value, weekEnd: weekEnd.value, top: top.value }),
      getWeeklyCertificates({ weekStart: weekStart.value, weekEnd: weekEnd.value })
    ])
    leaderboard.value = lb || []
    certificates.value = cert || []
  } catch (e) {
    toast.error(e?.response?.data?.message || 'Không tải được dữ liệu tuần')
  } finally {
    loading.value = false
  }
}

async function generate () {
  loading.value = true
  try {
    await generateWeeklyCertificates({ weekStart: weekStart.value, weekEnd: weekEnd.value, top: top.value })
    toast.success('Generate certificates thành công')
    await loadAll()
  } catch (e) {
    toast.error(e?.response?.data?.message || 'Generate thất bại')
  } finally {
    loading.value = false
  }
}

onMounted(loadAll)
</script>

<style scoped>
.admin-page { padding: 24px; display: grid; gap: 14px; }
.card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
}
.toolbar {
  display: flex;
  gap: 10px;
  align-items: end;
  flex-wrap: wrap;
}
label { display: grid; gap: 6px; font-weight: 600; }
input { border: 1px solid #d6d3d1; border-radius: 8px; padding: 7px 8px; }
button {
  border: none;
  border-radius: 10px;
  background: #ea580c;
  color: #fff;
  padding: 9px 12px;
  font-weight: 700;
  cursor: pointer;
}
button:disabled { opacity: 0.6; cursor: not-allowed; }
table { width: 100%; border-collapse: collapse; }
th, td { border-bottom: 1px solid #e7e5e4; padding: 8px; text-align: left; }
</style>
