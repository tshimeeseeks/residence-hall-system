package com.rhs.backend.service;

import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.model.SleepOverPass;
import com.rhs.backend.model.Visitor;
import com.rhs.backend.repository.SleepoverPassRepository;
import com.rhs.backend.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SleepOverService {

    private final SleepoverPassRepository sleepoverPassRepository;
    private final VisitorRepository visitorRepository;

    @Autowired
    public SleepOverService(SleepoverPassRepository sleepoverPassRepository, VisitorRepository visitorRepository) {
        this.sleepoverPassRepository = sleepoverPassRepository;
        this.visitorRepository = visitorRepository;
    }

    public SleepOverPass createSleepoverPass(SleepOverPass sleepoverPass) throws Exception {
        if (isGuestBlacklisted(sleepoverPass.getVisitor())) {
            throw new Exception("Guest is blacklisted.");
        }
        if (hasDateConflict(sleepoverPass)) {
            throw new Exception("Date conflict detected.");
        }
        visitorRepository.save(sleepoverPass.getVisitor());
        return sleepoverPassRepository.save(sleepoverPass);
    }

    public SleepOverPass reviewSleepoverPass(String id, AccountStatus status) throws Exception {
        Optional<SleepOverPass> optionalSleepoverPass = sleepoverPassRepository.findById(id);
        if (optionalSleepoverPass.isPresent()) {
            SleepOverPass sleepoverPass = optionalSleepoverPass.get();
            sleepoverPass.setStatus(status);
            return sleepoverPassRepository.save(sleepoverPass);
        } else {
            throw new Exception("Sleepover pass not found.");
        }
    }

    private boolean hasDateConflict(SleepOverPass newPass) {
        List<SleepOverPass> existingPasses = sleepoverPassRepository.findAll();
        for (SleepOverPass existingPass : existingPasses) {
            if (existingPass.getApplicantId().equals(newPass.getApplicantId()) &&
                    !existingPass.getStatus().equals(AccountStatus.REJECTED) &&
                    (newPass.getStartDate().isBefore(existingPass.getEndDate())
                            && newPass.getEndDate().isAfter(existingPass.getStartDate()))) {
                return true;
            }
        }
        return false;
    }

    private boolean isGuestBlacklisted(Visitor visitor) {
        // In a real application, you would have a separate blacklist collection
        // For now, we'll just check against a hardcoded list
        return "BLACKLISTED_ID".equals(visitor.getIdNumber());
    }

    public List<SleepOverPass> getMySleepoverPasses(String applicantId) {
        return sleepoverPassRepository.findAll().stream()
                .filter(pass -> pass.getApplicantId().equals(applicantId))
                .toList();
    }

    public List<SleepOverPass> getAllSleepoverPasses() {
        return sleepoverPassRepository.findAll();
    }

    public Optional<SleepOverPass> getSleepoverPassById(String id) {
        return sleepoverPassRepository.findById(id);
    }
}
