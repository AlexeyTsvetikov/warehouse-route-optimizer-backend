package ru.tsvetikov.warehouse.router.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.tsvetikov.warehouse.router.model.dto.response.StockResponse;
import ru.tsvetikov.warehouse.router.service.StockService;

import java.util.List;

@Tag(name = "Stocks", description = "Управление остатками товаров на складе")
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "Получить остатки по товару")
    @GetMapping("/product/{productId}")
    public List<StockResponse> getByProduct(@PathVariable Long productId) {
        return stockService.getByProduct(productId);
    }

    @Operation(summary = "Получить все остатки с фильтрацией по ячейке и поиском")
    @GetMapping
    public Page<StockResponse> getAll(
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer perPage
    ) {
        return stockService.getFiltered(location, search, page, perPage);
    }
}
