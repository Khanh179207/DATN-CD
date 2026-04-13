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
        {{ $t('leaderboard.hero_dish_toggle') }}
      </button>

      <button
        :class="{ active: mode === 'user' }"
        @click="changeMode('user')"
      >
        {{ $t('leaderboard.hero_user_toggle') }}
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
              {{ mode === 'user' ? $t('leaderboard.hero_grand_master') : $t('leaderboard.hero_masterpiece') }}
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
              <span class="author-label">{{ $t('leaderboard.hero_author_label') }}</span>
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
                    ? $t('leaderboard.hero_view_profile')
                    : $t('leaderboard.hero_view_masterpiece') }}
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
import { useI18n } from 'vue-i18n'
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
const { t, locale } = useI18n()
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
  return d.name || d.title || t('common.unknown')
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

const defaultDescription = computed(() => t('leaderboard.hero_default_description'))

/* =========================
   META LOGIC
========================= */

const computedMeta = computed(() => {
  const d = displayData.value
  if (!d) return {}

  if (mode.value === 'user') {
    return {
      [t('leaderboard.hero_meta_masterpieces')]: d.postCount ?? 0,
      [t('leaderboard.hero_meta_fans')]: (d.followers ?? 0).toLocaleString(getLocaleCode())
    }
  }

  return {
    [t('leaderboard.hero_meta_views')]: (d.views ?? 0).toLocaleString(getLocaleCode()),
    [t('leaderboard.hero_meta_difficulty')]: d.difficulty ?? t('leaderboard.hero_difficulty_medium')
  }
})

const getLocaleCode = () => (locale.value === 'vi' ? 'vi-VN' : 'en-US')

/* =========================
   AUTHOR DATA (REAL SAFE)
========================= */

const authorData = computed(() => {
  const d = props.topDish
  if (!d) return null

  const id =
    d.authorId ||
    d.userId ||
    d.accountId ||
    d.accountID ||
    d.author?.id

  const name =
    d.authorName ||
    d.chefName ||
    d.username ||
    d.author?.name

  const avatar =
    d.authorAvatar ||
    d.chefAvatar ||
    d.avatar ||
    d.author?.avatar

  // Nếu không có tên thì không hiển thị block tác giả
  if (!name) return null

  return { id, name, avatar }
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