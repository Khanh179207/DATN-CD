<template>
  <div class="app-container" :class="{ 'is-dark-theme': route.meta?.isDark }">
    
    <Transition name="fade-loader">
      <div v-if="isLoading" class="app-preloader">
        
        <div class="hearth-fire"></div>
        <div class="ambient-orb ambient-1"></div>
        <div class="ambient-orb ambient-2"></div>
        
        <div class="magic-dust-container">
          <div v-for="i in 12" :key="'dust-'+i" class="magic-dust"></div>
        </div>

        <div class="loader-content">
          <div class="logo-wrapper">
            <h2 class="loader-logo shine-text">GOMET</h2>
          </div>
          
          <p class="loader-text">CHÀO MỪNG TỚI VỚI GOMET</p>

          <div class="progress-wrapper">
            <div class="progress-track">
              <div class="loader-progress" ref="progressBarRef">
                <div class="progress-glow-tip"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <Sidebar 
      class="fixed-sidebar" 
      @open-premium="showPremium = true" 
      @logout="handleLogout" 
    />

    <div class="main-content" id="main-scroll-container">
      <Header 
        @open-premium="showPremium = true" 
        @open-login="openAuth('login')" 
        @open-register="openAuth('register')" 
        @logout="handleLogout"
      />

      <div class="page-body">
        <router-view v-slot="{ Component, route: currentRoute }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="currentRoute.fullPath" />
          </transition>
        </router-view>
      </div>

      <TheFooter />
    </div>

    <ChatSidebar />
    <MiniChatBox />
    <CompareFloatingBar />
    <GometAiChat ref="aiChatRef" />

    <button class="float-ai-btn" @click="openAiChat" title="Chat with Gomet AI">
      <div class="ai-icon-bg">
        <span class="icon">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
        </span>
      </div>
      <span class="label">GOMET AI</span>
    </button>

    <Teleport to="body">
       <AuthModal v-if="showAuthModal" :initial-view="modalTab" @close="showAuthModal = false" />
       <PremiumModal :is-open="showPremium" @close="handleClosePremium" @upgraded="handleUpgraded" @start-test-timer="handleStartTestTimer" />
       <ExpiredModal :is-open="showExpired" @renew="handleRenew" @cancel="handleCancel" />
       
       <MealplanModal 
         v-if="showMealplanModal" 
         :post-data="mealplanData" 
         @close="showMealplanModal = false" 
       />
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useChatStore } from '@/stores/chat' 
import { useAuthStore } from '@/stores/auth'
import { toast } from '@/composables/useToast' 
import gsap from 'gsap' 
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import { ensureBrowserNotificationPermission, showBrowserNotification } from '@/services/browserNotificationService'

import Sidebar from '@/components/sidebar/Sidebar.vue'
import Header from '@/components/topbar/Header.vue' 
import AuthModal from '@/components/modals/AuthModal.vue'
import PremiumModal from '@/components/modals/PremiumModal.vue'
import ExpiredModal from '@/components/modals/ExpiredModal.vue'
// 🔥 IMPORT MEALPLAN MODAL
import MealplanModal from '@/components/modals/MealplanModal.vue'

import MiniChatBox from '@/components/chat/MiniChatBox.vue'
import ChatSidebar from '@/components/chat/ChatSidebar.vue'
import TheFooter from '@/components/footer/TheFooter.vue'
import CompareFloatingBar from '@/components/common/CompareFloatingBar.vue'
import GometAiChat from '@/components/chat/GometAiChat.vue'

gsap.registerPlugin(ScrollTrigger)

const router = useRouter()
const route = useRoute()
const chatStore = useChatStore() 
const authStore = useAuthStore()

// --- 🚀 LOGIC LOADING TỐI ƯU ---
const isLoading = ref(false)
const progressBarRef = ref(null)

let ctx; 
let safetyTimer;

const startLoadingAnimation = () => {
  isLoading.value = true;
  sessionStorage.removeItem('just_logged_in'); 

  // LƯỚI AN TOÀN: Chống đơ tuyệt đối
  clearTimeout(safetyTimer);
  safetyTimer = setTimeout(() => {
    if (isLoading.value) isLoading.value = false;
  }, 4000);

  nextTick(() => {
    if (ctx) ctx.revert(); 
    
    ctx = gsap.context(() => {
      // Chỉ animate thanh progress cho đơn giản
      gsap.to(progressBarRef.value, { 
        width: '100%', 
        duration: 2.2, 
        ease: 'power2.inOut',
        onComplete: () => {
          clearTimeout(safetyTimer);
          setTimeout(() => { isLoading.value = false; }, 400);
        }
      });
    });
  });
};

// --- CÁC TRẠNG THÁI ---
const showAuthModal = ref(false); 
const showPremium = ref(false); 
const showExpired = ref(false); 
const isEnforcingRenewal = ref(false); 
const modalTab = ref('login'); 
const aiChatRef = ref(null);

// 🔥 TRẠNG THÁI CHO MEALPLAN MODAL
const showMealplanModal = ref(false);
const mealplanData = ref(null);
const chatRealtimeClient = ref(null)
const chatRealtimeConnecting = ref(false)

const getCurrentUserId = () => {
  const id = authStore.user?.accountID || authStore.user?.id
  return id ? Number(id) : null
}

const getChatAuthHeaders = () => ({
  Authorization: `Bearer ${authStore.user?.token || ''}`
})

const playChatNotificationSound = () => {
  try {
    const audio = new Audio('/sounds/notification.mp3')
    audio.volume = 0.6
    audio.play().catch((err) => {
      console.warn('Không thể tự động phát âm thanh chat:', err)
    })
  } catch (error) {
    console.error('Lỗi phát âm thanh chat:', error)
  }
}

const showChatBrowserNotification = (message) => {
  if (!('Notification' in window)) return

  const conversationId = Number(message?.conversation?.conversationID)
  const senderName = message?.sender?.username || 'Tin nhắn mới'
  const senderAvatar = message?.sender?.avatar || 'https://ui-avatars.com/api/?name=User&background=EA580C&color=fff'
  const body = message?.content || 'Bạn có tin nhắn mới'
  const icon = senderAvatar

  const openConversationFromNotification = () => {
    if (!Number.isFinite(conversationId)) return

    window.focus()
    chatStore.openChat({
      id: conversationId,
      conversationID: conversationId,
      name: senderName,
      avatar: senderAvatar,
      online: true
    })
  }

  const createDesktopNotification = () => {
    const notification = new Notification(senderName, { body, icon })
    notification.onclick = () => {
      openConversationFromNotification()
      notification.close()
    }
  }

  if (Notification.permission === 'granted') {
    createDesktopNotification()
    return
  }

  if (Notification.permission === 'default') {
    Notification.requestPermission().then((permission) => {
      if (permission === 'granted') {
        createDesktopNotification()
      }
    })
  }
}

const shouldNotifyGlobally = (message) => {
  const senderId = Number(message?.sender?.accountID || message?.senderID)
  const currentUserId = getCurrentUserId()

  if (!currentUserId || senderId === currentUserId) return false

  const incomingConversationId = Number(message?.conversation?.conversationID)
  const activeConversationId = Number(chatStore.activeChat?.id || chatStore.activeChat?.conversationID)

  // Nếu đang mở đúng cuộc chat thì MiniChatBox đã xử lý sound/notification riêng.
  return incomingConversationId !== activeConversationId
}

const handleGlobalIncomingChat = (message) => {
  if (!shouldNotifyGlobally(message)) return

  playChatNotificationSound()
  showChatBrowserNotificationUnified(message)
  chatStore.setUnreadCount((chatStore.totalUnreadCount || 0) + 1)
}

const cleanupChatSubscriptions = () => {
  chatSubscriptions.value.forEach((subscription) => {
    try {
      subscription.unsubscribe()
    } catch (error) {
      console.warn('Lỗi unsubscribe chat topic:', error)
    }
  })
  chatSubscriptions.value.clear()
}

const syncChatTopicSubscriptions = async () => {
  if (!chatRealtimeClient.value?.connected) return
  const userId = getCurrentUserId()
  if (!userId) return

  try {
    const res = await api.get(`/api/conversations/user/${userId}`)
    const conversations = Array.isArray(res.data) ? res.data : []
    const activeTopicSet = new Set(
      conversations
        .map((c) => Number(c.id || c.conversationID))
        .filter((id) => Number.isFinite(id))
        .map((id) => `/topic/${id}`)
    )

    // Unsubscribe các topic không còn tồn tại
    Array.from(chatSubscriptions.value.keys()).forEach((topic) => {
      if (!activeTopicSet.has(topic)) {
        try {
          chatSubscriptions.value.get(topic)?.unsubscribe()
        } catch (error) {
          console.warn('Lỗi bỏ subscribe topic:', error)
        }
        chatSubscriptions.value.delete(topic)
      }
    })

    // Subscribe các topic mới
    activeTopicSet.forEach((topic) => {
      if (chatSubscriptions.value.has(topic)) return
      const subscription = chatRealtimeClient.value.subscribe(topic, (payload) => {
        try {
          const incomingMessage = JSON.parse(payload.body)
          handleGlobalIncomingChat(incomingMessage)
        } catch (error) {
          console.error('Lỗi parse global chat message:', error)
        }
      })
      chatSubscriptions.value.set(topic, subscription)
    })
  } catch (error) {
    console.error('Lỗi đồng bộ chat topics:', error)
  }
}

const disconnectGlobalChatRealtime = () => {
  if (chatSyncTimer.value) {
    clearInterval(chatSyncTimer.value)
    chatSyncTimer.value = null
  }

  cleanupChatSubscriptions()

  if (chatRealtimeClient.value) {
    try {
      chatRealtimeClient.value.disconnect()
    } catch (error) {
      console.warn('Lỗi đóng global chat socket:', error)
    }
    chatRealtimeClient.value = null
  }
}

const connectGlobalChatRealtime = () => {
  if (!authStore.isAuthenticated || !authStore.user?.token || chatRealtimeClient.value?.connected) return

  const socket = new SockJS('http://localhost:8080/ws-chat')
  chatRealtimeClient.value = Stomp.over(socket)
  chatRealtimeClient.value.debug = () => {}

  chatRealtimeClient.value.connect(
    getChatAuthHeaders(),
    async () => {
      await syncChatTopicSubscriptions()
      if (chatSyncTimer.value) clearInterval(chatSyncTimer.value)
      chatSyncTimer.value = setInterval(syncChatTopicSubscriptions, 15000)
    },
    () => {
      disconnectGlobalChatRealtime()
      setTimeout(() => {
        if (authStore.isAuthenticated) connectGlobalChatRealtime()
      }, 3000)
    }
  )
}

const showChatBrowserNotificationUnified = (message) => {
  const conversationId = Number(message?.conversation?.conversationID)
  const messageId = Number(message?.messageID)
  const senderId = Number(message?.sender?.accountID || message?.senderID)
  const senderName = message?.sender?.username || 'Tin nháº¯n má»›i'
  const senderAvatar = message?.sender?.avatar || 'https://ui-avatars.com/api/?name=User&background=EA580C&color=fff'
  const body = message?.content || 'Báº¡n cÃ³ tin nháº¯n má»›i'

  showBrowserNotification({
    title: senderName,
    body,
    icon: senderAvatar,
    tag: messageId ? `chat-${messageId}` : `chat-${conversationId}`,
    dedupeKey: messageId
      ? `chat:${messageId}`
      : `chat:${conversationId}:${senderId}:${message?.createdAt || body}`,
    onClick: () => {
      if (!Number.isFinite(conversationId)) return
      chatStore.openChat({
        id: conversationId,
        conversationID: conversationId,
        name: senderName,
        avatar: senderAvatar,
        online: true
      })
    }
  })
}

const disconnectUnifiedGlobalChatRealtime = () => {
  if (chatRealtimeClient.value) {
    try {
      chatRealtimeClient.value.disconnect()
    } catch (error) {
      console.warn('Lá»—i Ä‘Ã³ng global chat socket:', error)
    }
    chatRealtimeClient.value = null
  }
  chatRealtimeConnecting.value = false
}

const connectUnifiedGlobalChatRealtime = () => {
  if (!authStore.isAuthenticated || !authStore.user?.token || chatRealtimeClient.value?.connected || chatRealtimeConnecting.value) {
    return
  }

  const userId = getCurrentUserId()
  if (!userId) return

  chatRealtimeConnecting.value = true

  const socket = new SockJS('http://localhost:8080/ws-chat')
  chatRealtimeClient.value = Stomp.over(socket)
  chatRealtimeClient.value.debug = () => {}

  chatRealtimeClient.value.connect(
    getChatAuthHeaders(),
    () => {
      chatRealtimeConnecting.value = false
      chatRealtimeClient.value.subscribe(`/topic/chat-user/${userId}`, (payload) => {
        try {
          const incomingMessage = JSON.parse(payload.body)
          handleGlobalIncomingChat(incomingMessage)
        } catch (error) {
          console.error('Lá»—i parse global chat message:', error)
        }
      })
    },
    () => {
      disconnectUnifiedGlobalChatRealtime()
      setTimeout(() => {
        if (authStore.isAuthenticated) connectUnifiedGlobalChatRealtime()
      }, 3000)
    }
  )
}

const handleStartTestTimer = () => { setTimeout(() => { showExpired.value = true; isEnforcingRenewal.value = true; }, 12000); };
const handleRenew = () => { showExpired.value = false; showPremium.value = true; };
const handleClosePremium = () => { showPremium.value = false; if (isEnforcingRenewal.value) { showExpired.value = true; toast.error("Bạn cần gia hạn Premium để tiếp tục sử dụng các tính năng cao cấp!"); } };
const handleUpgraded = () => { isEnforcingRenewal.value = false; showPremium.value = false; showExpired.value = false; };
const handleCancel = () => { showExpired.value = false; isEnforcingRenewal.value = false; };

// --- HÀM XỬ LÝ SỰ KIỆN MỞ MEALPLAN ---
const handleOpenMealplan = (event) => {
  // Lấy data truyền tới (nếu có, ví dụ post chi tiết)
  mealplanData.value = event.detail?.post || null;
  showMealplanModal.value = true;
};

// --- HÀM XỬ LÝ KHÔI PHỤC TÀI KHOẢN ---
const handleRestorePrompt = (e) => {
  modalTab.value = 'restore-account';
  showAuthModal.value = true;
  // 🔥 Đã FIX: Gửi ngược dữ liệu vào AuthModal sau khi nó được Mount (v-if)
  nextTick(() => {
    window.dispatchEvent(new CustomEvent('auth:restore-login-data', { detail: e.detail }));
  });
};



onMounted(() => {
  if (sessionStorage.getItem('just_logged_in') === 'true') startLoadingAnimation();
  ensureBrowserNotificationPermission()
  
  /* Global Event Listeners */
  window.addEventListener('ui:open-premium', () => { showPremium.value = true; })
  
  // 🔥 LẮNG NGHE CÁC SỰ KIỆN TỪ HỆ THỐNG
  window.addEventListener('ui:open-mealplan', handleOpenMealplan)
  window.addEventListener('auth:restore-login-prompt', handleRestorePrompt)

  if (authStore.isAuthenticated) {
    connectUnifiedGlobalChatRealtime()
  }
})

watch(() => route.fullPath, () => {
  document.body.style.overflow = '';
  document.documentElement.style.overflow = '';
  ScrollTrigger.refresh();
  const mainScroll = document.getElementById('main-scroll-container');
  if (mainScroll) mainScroll.scrollTo({ top: 0 });
  if (sessionStorage.getItem('just_logged_in') === 'true') startLoadingAnimation();
})

onUnmounted(() => {
  clearTimeout(safetyTimer);
  if (ctx) ctx.revert();
  disconnectUnifiedGlobalChatRealtime();
  // 🔥 DỌN DẸP EVENT LISTENER
  window.removeEventListener('ui:open-mealplan', handleOpenMealplan);
  window.removeEventListener('auth:restore-login-prompt', handleRestorePrompt);
})

watch(
  () => authStore.isAuthenticated,
  (isAuthenticated) => {
    if (isAuthenticated) {
      connectUnifiedGlobalChatRealtime()
    } else {
      disconnectUnifiedGlobalChatRealtime()
    }
  }
)

// --- LOGIC LOCK PREMIUM CHO NÚT GOMET ASSISTANT ---
const openAiChat = () => { 
  const userStr = localStorage.getItem('user');
  let isPremiumUser = false;
  
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      const isPremium = user?.isPremium || user?.IsPremium || user?.role === 'PREMIUM' || user?.role === 'premium';
      const isAdmin = user?.isAdmin || user?.IsAdmin || user?.role === 'ADMIN' || user?.role === 'admin';
      isPremiumUser = isPremium || isAdmin; 
    } catch (e) {
      console.error('Lỗi parse user data', e);
    }
  }

  if (!isPremiumUser) {
    showPremium.value = true;
    toast.warn("Gomet Assistant là tính năng đặc quyền dành riêng cho tài khoản Premium sếp nhé!");
    return;
  }

  if (aiChatRef.value) aiChatRef.value.openChat();
};

const openAuth = (tab) => { modalTab.value = tab; showAuthModal.value = true; };
const handleLogout = async () => { localStorage.removeItem('user'); localStorage.removeItem('token'); sessionStorage.removeItem('just_logged_in'); await router.push('/'); };
</script>

<style scoped>
.app-preloader {
  position: fixed; 
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  background-color: #FFF7ED; 
  z-index: 99999;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  overflow: hidden;
}

.is-dark-theme .app-preloader { background-color: #050505; }

.hearth-fire {
  position: absolute; bottom: -20vh; left: 0; width: 100%; height: 40vh;
  background: radial-gradient(ellipse at bottom, rgba(234, 88, 12, 0.4) 0%, transparent 70%);
  filter: blur(40px); animation: firePulse 3s infinite alternate; z-index: 0;
}

.ambient-orb { position: absolute; border-radius: 50%; filter: blur(120px); opacity: 0.6; z-index: 1; animation: floatOrb 8s infinite alternate ease-in-out; }
.ambient-1 { width: 50vw; height: 50vw; background: radial-gradient(circle, rgba(234, 88, 12, 0.35) 0%, transparent 70%); top: -20%; left: -10%; }
.ambient-2 { width: 60vw; height: 60vw; background: radial-gradient(circle, rgba(245, 158, 11, 0.25) 0%, transparent 70%); bottom: -30%; right: -15%; animation-delay: -4s; }

.magic-dust-container { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 2; pointer-events: none; }
.magic-dust { position: absolute; bottom: -20px; width: 5px; height: 5px; background-color: #ffffff; border-radius: 50%; box-shadow: 0 0 15px 5px rgba(253, 186, 116, 0.9); opacity: 0; animation: magicFly 4s infinite cubic-bezier(0.4, 0, 0.2, 1); }

.loader-content { position: relative; z-index: 10; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 20px; transform: translateY(-5vh); }

.loader-logo { font-family: 'Playfair Display', serif; font-size: 6rem; font-weight: 900; letter-spacing: 12px; margin: 0; color: #EA580C; position: relative; }
.shine-text::after { content: "GOMET"; position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(100deg, #EA580C 20%, #F59E0B 40%, #FCD34D 60%, #EA580C 80%); background-size: 200% auto; -webkit-background-clip: text; -webkit-text-fill-color: transparent; animation: shineText 3.5s linear infinite; }

.loader-text { color: #9A3412; font-size: 1.1rem; font-weight: 700; letter-spacing: 4px; text-transform: uppercase; margin: 0; animation: breathe 2s infinite alternate; }
.is-dark-theme .loader-text { color: #FDBA74; }

.progress-wrapper { width: 320px; padding: 10px 0; }
.progress-track { width: 100%; height: 4px; background: #FFEDD5; border-radius: 10px; position: relative; overflow: visible; }
.is-dark-theme .progress-track { background: rgba(234, 88, 12, 0.2); }
.loader-progress { height: 100%; width: 0%; background: linear-gradient(90deg, #EA580C, #FCD34D); border-radius: 10px; position: relative; box-shadow: 0 0 15px rgba(234, 88, 12, 0.8); }
.progress-glow-tip { position: absolute; right: -6px; top: 50%; transform: translateY(-50%); width: 14px; height: 14px; background-color: #ffffff; border-radius: 50%; box-shadow: 0 0 12px 3px #ffffff, 0 0 25px 8px #FCD34D; }

@keyframes firePulse { 0% { transform: scaleY(1); opacity: 0.6; } 100% { transform: scaleY(1.3); opacity: 1; } }
@keyframes floatOrb { 0% { transform: translate(0, 0) scale(1); } 100% { transform: translate(30px, -20px) scale(1.1); } }
@keyframes magicFly { 0% { transform: translateY(0) scale(0.5); opacity: 0; } 100% { transform: translateY(-40vh) translateX(20px) scale(1.5); opacity: 0; } }
@keyframes shineText { to { background-position: 200% center; } }
@keyframes breathe { 0% { opacity: 0.5; } 100% { opacity: 1; } }
.fade-loader-leave-to { opacity: 0; transform: scale(1.1); }

/* --- CSS CŨ --- */
.app-container { display: flex; height: 100vh; overflow: hidden; background-color: var(--color-neutral-0); font-family: var(--font-body); color: var(--color-neutral-900); position: relative; transition: background-color 0.4s ease; }
.app-container.is-dark-theme { background-color: #000000 !important; }
.fixed-sidebar { flex-shrink: 0; z-index: var(--z-toast); }
.main-content { flex: 1; display: flex; flex-direction: column; height: 100%; overflow-y: auto; scroll-behavior: smooth; position: relative; }
.is-dark-theme .page-body { margin-top: calc(-1 * var(--header-height, 80px)); }
.page-body { padding: 0; flex: 1; position: relative; width: 100%; }
.page-fade-enter-active, .page-fade-leave-active { transition: opacity var(--duration-normal) var(--ease-out), transform var(--duration-normal) var(--ease-out); }
.page-fade-enter-from { opacity: 0; transform: translateY(10px); }
.page-fade-leave-to   { opacity: 0; transform: translateY(-10px); }

.float-ai-btn { 
  position: fixed; bottom: var(--space-8); right: var(--space-8); z-index: 99; 
  display: flex; align-items: center; 
  gap: 0; 
  padding: var(--space-2); 
  background: var(--color-neutral-0); border: 1px solid var(--color-neutral-200); 
  border-radius: var(--radius-full); box-shadow: var(--shadow-lg); cursor: pointer; 
  transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1); 
}
.float-ai-btn:hover {
  gap: var(--space-3);
  padding: var(--space-2) var(--space-5) var(--space-2) var(--space-2);
}

.is-dark-theme .float-ai-btn { background: rgba(255, 255, 255, 0.1); border-color: rgba(255, 255, 255, 0.2); backdrop-filter: blur(10px); }

.ai-icon-bg { width: 44px; height: 44px; background: linear-gradient(135deg, var(--color-primary-600), var(--color-warning)); border-radius: var(--radius-full); display: flex; align-items: center; justify-content: center; color: var(--color-neutral-0); font-size: var(--text-lg); box-shadow: var(--shadow-primary-md); flex-shrink: 0; }

.label { 
  font-weight: var(--font-extrabold); 
  font-size: var(--text-base); 
  color: var(--color-primary-700); 
  max-width: 0; 
  opacity: 0; 
  white-space: nowrap; 
  overflow: hidden; 
  transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1); 
}

.float-ai-btn:hover .label { 
  max-width: 200px; 
  opacity: 1; 
}
</style>
