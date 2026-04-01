package poly.edu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.entity.Account;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountDAO accountDAO;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tìm User trong DB dựa vào Email (Vì JWT của sếp đang lưu Email)
        Account acc = accountDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + email));

        // 2. PHIÊN DỊCH: isAdmin = 1 thì cấp thẻ ROLE_ADMIN, còn lại là ROLE_USER
        String roleName = (acc.getIsAdmin() != null && acc.getIsAdmin() == 1) ? "ROLE_ADMIN" : "ROLE_USER";

        // 3. Đóng gói thông tin lại cho Spring Security
        return new User(
                acc.getEmail(), // Dùng email làm username cho Security hiểu
                acc.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName)) // 🔥 Thẻ từ quyền lực nằm ở đây
        );
    }
}