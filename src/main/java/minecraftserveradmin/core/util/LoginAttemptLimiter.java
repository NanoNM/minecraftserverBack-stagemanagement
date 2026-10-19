package minecraftserveradmin.core.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录失败次数限制，用于防止密码爆破。
 * <p>
 * 以用户名作为计数键：连续失败达到上限后锁定一段时间，锁定期间直接拒绝登录；
 * 登录成功后清零。计数保存在内存中，重启后失效。
 */
@Component
public class LoginAttemptLimiter {

    /** 允许的连续失败次数 */
    @Value("${MCM.login.maxAttempts:5}")
    private int maxAttempts;

    /** 达到上限后的锁定时长（分钟） */
    @Value("${MCM.login.lockMinutes:15}")
    private long lockMinutes;

    private static class Attempt {
        int count;
        long lockUntil;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    /**
     * 判断该用户名当前是否处于锁定状态
     */
    public boolean isLocked(String key) {
        if (key == null) {
            return false;
        }
        Attempt attempt = attempts.get(key);
        if (attempt == null) {
            return false;
        }
        if (attempt.lockUntil > System.currentTimeMillis()) {
            return true;
        }
        if (attempt.lockUntil != 0) {
            attempts.remove(key, attempt);
        }
        return false;
    }

    /**
     * 记录一次登录失败，达到上限时开始锁定
     */
    public void recordFailure(String key) {
        if (key == null) {
            return;
        }
        long now = System.currentTimeMillis();
        attempts.compute(key, (k, attempt) -> {
            if (attempt == null) {
                attempt = new Attempt();
            }
            // 上一次锁定已过期，重新计数
            if (attempt.lockUntil != 0 && attempt.lockUntil <= now) {
                attempt.count = 0;
                attempt.lockUntil = 0;
            }
            attempt.count++;
            if (attempt.count >= maxAttempts) {
                attempt.lockUntil = now + lockMinutes * 60_000L;
                attempt.count = 0;
            }
            return attempt;
        });
        purgeIfNeeded(now);
    }

    /**
     * 登录成功后清除该用户名的失败记录
     */
    public void reset(String key) {
        if (key != null) {
            attempts.remove(key);
        }
    }

    /**
     * 剩余锁定秒数，未锁定时返回 0
     */
    public long getRemainingLockSeconds(String key) {
        if (key == null) {
            return 0;
        }
        Attempt attempt = attempts.get(key);
        if (attempt == null) {
            return 0;
        }
        long remaining = attempt.lockUntil - System.currentTimeMillis();
        return remaining > 0 ? (remaining + 999) / 1000 : 0;
    }

    /**
     * 防止长期运行后 Map 无限增长
     */
    private void purgeIfNeeded(long now) {
        if (attempts.size() <= 1024) {
            return;
        }
        attempts.entrySet().removeIf(entry -> {
            Attempt attempt = entry.getValue();
            return attempt.lockUntil != 0 && attempt.lockUntil <= now;
        });
    }
}
