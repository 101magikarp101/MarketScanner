package org.magikarp.marketscanner.ticker;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.magikarp.marketscanner.watchlist.WatchTicker;
import org.magikarp.marketscanner.watchlist.WatchTickerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TickerController {

    private final WatchTickerRepository watchTickerRepository;

    public TickerController(WatchTickerRepository watchTickerRepository) {
        this.watchTickerRepository = watchTickerRepository;
    }

    @GetMapping("/add")
    public String addForm(Principal principal, Model model) {
        // /add is authenticated() in SecurityConfig, so principal should be non-null
        String owner = principal.getName();

        List<String> tickers = watchTickerRepository.findByOwnerOrderBySymbolAsc(owner).stream()
                .map(WatchTicker::getSymbol)
                .toList();

        model.addAttribute("tickers", tickers);
        return "add";
    }

    @PostMapping("/add")
    public String addTicker(
            @RequestParam("symbol") String symbol,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        String owner = principal.getName();

        String normalized = (symbol == null) ? "" : symbol.trim().toUpperCase(Locale.US);
        if (normalized.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ticker symbol is required.");
            return "redirect:/add";
        }

        try {
            if (watchTickerRepository.existsByOwnerAndSymbol(owner, normalized)) {
                redirectAttributes.addFlashAttribute("error", normalized + " already exists.");
                return "redirect:/add";
            }
            WatchTicker watchTicker = new WatchTicker();
            watchTicker.setSymbol(normalized);
            watchTicker.setOwner(owner);
            watchTickerRepository.save(watchTicker);
            redirectAttributes.addFlashAttribute("success", normalized + " added.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", normalized + " already exists.");
        }

        return "redirect:/add";
    }
}
