<template>
  <div class="home-page container">
    
    <section class="central-search">
      <div class="brand-center">
        <span class="brand-icon">🍳</span>
        <h1 class="brand-name">GOMET</h1>
      </div>
      
      <div class="big-search-bar">
        <span class="search-icon">🔍</span>
        <input type="text" placeholder="Tìm tên món hay nguyên liệu..." />
        <button class="btn-search">Tìm Kiếm</button>
      </div>
    </section>

    <section class="trending-section">
      <h3 class="section-title">Từ Khóa Thịnh Hành</h3>
      <div class="grid-recipes">
        <div class="recipe-card" style="background-image: url('https://placehold.co/300x150/orange/white?text=Banh+Ngot');">
          <div class="overlay">bánh ngọt đơn giản</div>
        </div>
        <div class="recipe-card" style="background-image: url('https://placehold.co/300x150/green/white?text=Heo+Quay');">
          <div class="overlay">món ngon từ heo</div>
        </div>
        <div class="recipe-card" style="background-image: url('https://placehold.co/300x150/red/white?text=Thit+Kho');">
          <div class="overlay">thịt ba chỉ</div>
        </div>
        <div class="recipe-card" style="background-image: url('https://placehold.co/300x150/blue/white?text=Canh+Chua');">
          <div class="overlay">canh chua</div>
        </div>
      </div>
    </section>

    <section class="recent-section">
      <h3 class="section-title">Món bạn mới xem gần đây</h3>
      <div style="padding: 20px; background: #f9fafb; border-radius: 10px; text-align: center; color: #888;">
        Chưa có món nào xem gần đây
      </div>
    </section>

  </div>
</template>

<style scoped>
.container { padding: 0 40px; max-width: 1000px; margin: 0 auto; }

/* CENTRAL SEARCH */
.central-search { text-align: center; margin-bottom: 50px; margin-top: 20px; }
.brand-center { display: flex; align-items: center; justify-content: center; gap: 10px; margin-bottom: 20px; }
.brand-icon { font-size: 2.5rem; }
.brand-name { font-size: 2.5rem; font-weight: 800; color: #333; margin: 0; }

.big-search-bar {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 5px;
  max-width: 600px;
  margin: 0 auto;
  background: white;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}
.big-search-bar input { flex: 1; border: none; outline: none; padding: 10px; font-size: 1rem; }
.search-icon { padding-left: 15px; color: #999; }
.btn-search {
  background: #F97316; color: white; border: none;
  padding: 10px 25px; border-radius: 6px;
  font-weight: bold; cursor: pointer;
}

/* GRID RECIPES */
.section-title { font-size: 1.1rem; color: #4b5563; margin-bottom: 15px; font-weight: bold; }
.grid-recipes {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* Chia 4 cột */
  gap: 15px;
  margin-bottom: 40px;
}
.recipe-card {
  height: 120px;
  border-radius: 8px;
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}
.overlay {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: white;
  padding: 8px;
  font-weight: bold;
  font-size: 0.9rem;
}
</style>