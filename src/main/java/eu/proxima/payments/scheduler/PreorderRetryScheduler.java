package eu.proxima.payments.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eu.proxima.payments.enums.PreorderStatus;
import eu.proxima.payments.model.TransactionPreorder;
import eu.proxima.payments.repositories.TransactionEntityRepository;
import eu.proxima.payments.service.TransactionPreorderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PreorderRetryScheduler {

    private final TransactionEntityRepository transactionRep;
    private final TransactionPreorderService preorderService;

    // Runs every minute by default; configurable via property
    @Scheduled(fixedDelayString = "${preorder.retry.delay:60000}")
    public void retryAuthorizedPreorders() {
        log.debug("Scheduler: looking for AUTHORIZED preorders to retry...");
        List<TransactionPreorder> list = transactionRep.findByStatus(PreorderStatus.AUTHORIZED);
        for (TransactionPreorder p : list) {
            if (p.getRetryCount() >= p.getMaxRetries()) {
                log.info("Scheduler: skipping preorder id={} because retryCount >= maxRetries ({} >= {})", p.getId(),
                        p.getRetryCount(), p.getMaxRetries());
                continue;
            }
            try {
                log.info("Scheduler: retrying preorder id={}", p.getId());
                preorderService.executePreorder(p.getId(), null);
            } catch (Exception e) {
                log.warn("Scheduler: retry failed for id={} -> {}", p.getId(), e.getMessage());
            }
        }
    }
}
