package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.SubscriptionDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Subscription;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * Premium subscription management.
 * POST /api/subscription/upgrade  – activate premium for an account
 * GET  /api/subscription/status/{accountID} – check current premium status
 */
@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@CrossOrigin
public class SubscriptionController {

    private final SubscriptionDAO subscriptionDAO;
    private final AccountDAO      accountDAO;

    /** Upgrade account to premium (plan: 1=monthly, 2=yearly). */
    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade(@RequestBody Map<String, Object> body) {
        Integer accountID = toInt(body.get("accountID"));
        Integer plan      = toInt(body.get("plan")); // 1=monthly 2=yearly

        if (accountID == null || plan == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "accountID and plan are required"));
        }

        Optional<Account> opt = accountDAO.findById(accountID);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Account not found"));
        }

        Account acc = opt.get();

        // Calculate subscription period
        LocalDate now = LocalDate.now();
        LocalDate end = (plan == 2) ? now.plusYears(1) : now.plusMonths(1);

        // Save subscription record
        Subscription sub = Subscription.builder()
                .account(acc)
                .planType(plan)
                .startAt(now)
                .endAt(end)
                .isActive(1)
                .build();
        subscriptionDAO.save(sub);

        // Mark account as premium
        acc.setIsPremium(1);
        accountDAO.save(acc);

        return ResponseEntity.ok(Map.of(
                "message",   "Premium activated successfully",
                "isPremium", 1,
                "plan",      plan,
                "startAt",   now.toString(),
                "endAt",     end.toString()
        ));
    }

    /** Check premium status of an account. */
    @GetMapping("/status/{accountID}")
    public ResponseEntity<?> status(@PathVariable Integer accountID) {
        Optional<Account> opt = accountDAO.findById(accountID);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Account acc = opt.get();
        return ResponseEntity.ok(Map.of(
                "isPremium", acc.getIsPremium() == 1,
                "accountID", acc.getAccountID()
        ));
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        if (val instanceof Integer) return (Integer) val;
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return null; }
    }
}
