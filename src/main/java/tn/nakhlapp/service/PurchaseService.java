package tn.nakhlapp.service;

import tn.nakhlapp.model.Cage;
import tn.nakhlapp.model.Operation;
import tn.nakhlapp.repository.CageProdRepository;
import tn.nakhlapp.repository.CageRepository;
import tn.nakhlapp.repository.OperationRepository;
import tn.nakhlapp.util.NumberFormatUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class PurchaseService {

    private final OperationRepository operationRepository = new OperationRepository();
    private final CageProdRepository cageProdRepository = new CageProdRepository();
    private final CageRepository cageRepository = new CageRepository();

    public record PurchaseCalculation(
            double grossWeight,
            double netWeight,
            double totalAmount,
            String unitPrice,
            double coefficient
    ) {
    }

    public Optional<String> resolveUnitPrice(int productId, int cageId) throws SQLException {
        return cageProdRepository.findBuyPrice(productId, cageId);
    }

    public PurchaseCalculation calculate(double grossWeight, double cageCount, int cageId, String unitPriceText)
            throws SQLException {
        double unitPrice = NumberFormatUtil.parseDouble(unitPriceText, 0);
        double coefficient = cageRepository.findById(cageId)
                .map(Cage::coefficient)
                .orElse(0d);
        double netWeight = grossWeight - (coefficient * cageCount);
        double total = netWeight * unitPrice;
        return new PurchaseCalculation(
                grossWeight,
                netWeight,
                total,
                NumberFormatUtil.format3(unitPrice),
                coefficient
        );
    }

    public int savePurchase(
            int clientId,
            int productId,
            int cageId,
            double grossWeight,
            double cageCount,
            String unitPrice,
            double coefficient
    ) throws SQLException {
        Operation operation = new Operation(
                0,
                LocalDate.now(),
                LocalTime.now().withNano(0),
                clientId,
                productId,
                cageId,
                NumberFormatUtil.format3(grossWeight),
                cageCount,
                unitPrice,
                coefficient
        );
        return operationRepository.insert(operation);
    }

    public List<Operation> recentOperations(int limit) throws SQLException {
        return operationRepository.findRecent(limit);
    }

    public void deleteOperation(int id) throws SQLException {
        operationRepository.delete(id);
    }
}
