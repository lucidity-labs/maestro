package org.example.workflow.model;

import java.math.BigDecimal;
import java.util.List;

public record Order(BigDecimal total, List<OrderedProduct> orderedProducts) {
}
