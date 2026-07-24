package br.com.ufca.sixsevenpayapi.application.dto;

import java.math.BigDecimal;

public record DashboardResponseDTO(
        long totalAccounts,
        long totalCustomers,
        BigDecimal totalBankBalance,
        long blockedAccounts,
        long pendingRequests
) {
}
