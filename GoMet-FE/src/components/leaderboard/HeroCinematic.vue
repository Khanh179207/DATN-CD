<template>
  <section
    v-if="displayData"
    class="hero-cinematic-supreme"
    ref="heroRef"
    @mousemove="onMouseMove"
  >
    <!-- BACKGROUND -->
    <div class="cinematic-bg-wrapper">
      <div
        :key="mode"
        class="cinematic-bg"
        ref="bgRef"
        :style="{ backgroundImage: `url(${backgroundImage})` }"
      ></div>
      <div class="cinematic-overlay"></div>
      <div class="light-leak-effect"></div>
      <div class="film-noise"></div>
    </div>

    <!-- TOGGLE -->
    <div class="leaderboard-toggle anim-premium">
      <button
        :class="{ active: mode === 'dish' }"
        @click="changeMode('dish')"
      >
        🏆 Tuyệt tác
      </button>

      <button
        :class="{ active: mode === 'user' }"
        @click="changeMode('user')"
      >
        👑 Người xuất sắc
      </button>
    </div>

    <!-- CONTENT -->
    <div class="cinematic-layout" :key="displayData.id">
      <div class="hero-visual">
        <div class="rank-overlay-huge">01</div>
      </div>

      <div class="hero-info">
        <div class="content-wrapper">

          <!-- LABEL -->
          <div class="rank-label anim-premium">
            <span class="rank-num-main">Nº 01</span>
            <span class="rank-type">
              {{ mode === 'user' ? 'Grand Master' : 'The Masterpiece' }}
            </span>

            <span v-if="displayData?.isPremium === 1" class="premium-star">
              ✦ PREMIUM
            </span>
          </div>

          <!-- TITLE -->
          <h1 class="hero-title-main anim-premium">
            <span class="italic-first">
              {{ getFirstChar(displayTitle) }}
            </span>
            <span class="rest-chars">
              {{ getRestChars(displayTitle) }}
            </span>
          </h1>

          <!-- DESCRIPTION -->
          <p class="hero-desc anim-premium">
            "{{ displayData.description || defaultDescription }}"
          </p>

          <!-- AUTHOR BLOCK (ONLY DISH MODE) -->
          <div
            v-if="mode === 'dish' && authorData"
            class="author-block-premium anim-premium"
            @click="goToAuthor"
          >
            <div class="avatar-wrapper">
              <img
                :src="authorData.avatar"
                :alt="authorData.name"
              />
              <div class="avt-glow"></div>
            </div>

            <div class="author-info">
              <span class="author-label">Tác giả tuyệt tác</span>
              <span class="author-name">
                {{ authorData.name }}
              </span>
            </div>
          </div>

          <!-- META -->
          <div class="hero-meta-grid anim-premium">
            <div
              class="meta-item"
              v-for="(val, lbl) in computedMeta"
              :key="lbl"
            >
              <span class="meta-label">{{ lbl }}</span>
              <span class="meta-value">{{ val }}</span>
            </div>
          </div>

          <!-- CTA -->
          <div class="hero-action anim-premium">
            <button
              class="btn-premium-cta"
              @click="goToDetail"
            >
              <div class="btn-content">
                <span>
                  {{ mode === 'user'
                    ? 'Khám phá hồ sơ'
                    : 'Xem chi tiết tuyệt tác' }}
                </span>

                <svg
                  class="icon-arrow"
                  width="22"
                  height="22"
                  viewBox="0 0 24 24"
                >
                  <path
                    d="M5 12h14M12 5l7 7-7 7"
                    stroke="currentColor"
                    fill="none"
                    stroke-width="2.5"
                  />
                </svg>
              </div>

              <div class="btn-shimmer"></div>
            </button>
          </div>

        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'

/* =========================
   PROPS
========================= */

const props = defineProps({
  topDish: { type: Object, default: null },
  topUser: { type: Object, default: null },
  // 'dishes' | 'chefs' - đồng bộ với CinematicList / Leaderboard
  modelValue: { type: String, default: 'dishes' }
})

const emit = defineEmits(['update:modelValue'])

const router = useRouter()
const bgRef = ref(null)
const heroRef = ref(null)

// mode nội bộ: 'dish' | 'user', được map từ modelValue ('dishes' | 'chefs')
const mode = computed(() =>
  props.modelValue === 'chefs' ? 'user' : 'dish'
)

/* =========================
   DISPLAY SWITCH
========================= */

const displayData = computed(() => {
  return mode.value === 'user'
    ? props.topUser
    : props.topDish
})

/* =========================
   SAFE TITLE
========================= */

const displayTitle = computed(() => {
  const d = displayData.value
  if (!d) return ''
  return d.name || d.title || 'Unknown'
})

const getFirstChar = (s) => s ? String(s).charAt(0) : ''
const getRestChars = (s) => s ? String(s).slice(1) : ''

/* =========================
   BACKGROUND SAFE
========================= */

const backgroundImage = computed(() => {
  const d = displayData.value
  if (!d) return ''
  return d.image || d.avatar || ''
})

/* =========================
   DEFAULT DESCRIPTION
========================= */

const defaultDescription =
  'Vinh danh tinh hoa ẩm thực và sự cống hiến xuất sắc tại GoMet.'

/* =========================
   META LOGIC - ĐÃ ĐỒNG BỘ ĐIỂM SỐ
========================= */
const computedMeta = computed(() => {
  const d = displayData.value
  if (!d) return {}

  if (mode.value === 'user') {
    return {
      'Tuyệt tác': d.postCount ?? 0,
      'Người hâm mộ': d.followers ?? 0,
      // 🔥 FIX TẠI ĐÂY: Dùng d.pts hoặc d.totalPts để lấy đúng dữ liệu từ Backend
      'Điểm cống hiến': (d.pts || d.totalPts) ? `${d.pts || d.totalPts} PTS` : '0 PTS'
    }
  }

  return {
    'Điểm tinh hoa': d.pts ? `${d.pts} PTS` : '0 PTS', 
    'Độ khó': d.difficulty ?? 'Trung bình'
  }
})

/* =========================
    AUTHOR DATA - ĐẢM BẢO CLICK ĐƯỢC
========================= */
const authorData = computed(() => {
  const d = props.topDish
  if (!d) return null


  return { 
    id: d.authorId, 
    name: d.authorName || 'GoMet Chef', 
    avatar: d.authorAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(d.authorName || 'G')}&background=EA580C&color=fff`
  }
})

/* =========================
   ROUTING
========================= */

const goToDetail = () => {
  const d = displayData.value
  if (!d) return

  if (mode.value === 'user') {
    router.push(`/profile/${d.id}`)
  } else {
    router.push(`/post/${d.id}`)
  }
}

const goToAuthor = () => {
  if (!authorData.value?.id) return
  router.push(`/profile/${authorData.value.id}`)
}

/* =========================
   MODE SWITCH
========================= */

const changeMode = (newMode) => {
  if (mode.value === newMode) return
  const newCategory = newMode === 'user' ? 'chefs' : 'dishes'
  emit('update:modelValue', newCategory)
}

/* =========================
   PARALLAX
========================= */

const onMouseMove = (e) => {
  if (!bgRef.value) return

  const x = (e.clientX / window.innerWidth - 0.5) * 35
  const y = (e.clientY / window.innerHeight - 0.5) * 35

  gsap.to(bgRef.value, {
    x,
    y,
    duration: 1.2,
    ease: 'power2.out'
  })
}

/* =========================
   ANIMATION
========================= */

const runEntranceAnimation = () => {
  gsap.fromTo(
    '.anim-premium',
    { opacity: 0, y: 40 },
    {
      opacity: 1,
      y: 0,
      stagger: 0.12,
      duration: 1,
      clearProps: 'all'
    }
  )
}

onMounted(() => {
  const tl = gsap.timeline({
    defaults: { ease: 'power2.out' }
  })

  tl.from('.cinematic-bg', {
    scale: 1.5,
    filter: 'brightness(0)',
    duration: 2.8
  })
    .to('.cinematic-overlay', {
      opacity: 1,
      duration: 2
    }, '-=2')
    .from('.rank-overlay-huge', {
      opacity: 0,
      x: -100,
      duration: 1.5
    }, '-=1.5')

  runEntranceAnimation()
})

watch(mode, () => {
  runEntranceAnimation()
})
</script>

<style scoped lang="scss" src="@/components/leaderboard/HeroCinematic.scss"></style>