<template>
  <div class="admin-layout" style="display: flex; min-height: 100vh;">
    
    <aside style="width: 250px; background-color: #1e293b; color: white; flex-shrink: 0;">
      <div style="padding: 20px; text-align: center; border-bottom: 1px solid #334155;">
        <h2 style="margin: 0; color: #F97316; font-weight: bold;">GOMET ADMIN</h2>
      </div>

      <nav style="padding: 20px 0;">
        <ul style="list-style: none; padding: 0; margin: 0;">
          <li v-for="item in menuItems" :key="item.path">
            <router-link 
              :to="item.path"
              style="display: block; padding: 12px 20px; color: #cbd5e1; text-decoration: none; transition: 0.3s;"
              active-class="active-link"
            >
              {{ item.name }}
            </router-link>
          </li>
        </ul>
      </nav>
    </aside>

    <div style="flex-grow: 1; display: flex; flex-direction: column;">
      
      <header style="background: white; padding: 15px 30px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center;">
        <h3 style="margin: 0; color: #333;">Trang quản trị</h3>
        <div style="display: flex; gap: 15px;">
          <span>Xin chào, <b>Admin</b></span>
          <button style="color: red; cursor: pointer; border: none; background: none;">Đăng xuất</button>
        </div>
      </header>

      <main style="padding: 30px; background-color: #f1f5f9; flex-grow: 1;">
                <div style="background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); min-height: 400px;">
                  <router-view />
                </div>
              </main>
            </div>
          </div>
        </template>