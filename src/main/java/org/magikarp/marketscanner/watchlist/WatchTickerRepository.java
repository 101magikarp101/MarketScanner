package org.magikarp.marketscanner.watchlist;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchTickerRepository extends JpaRepository<WatchTicker, WatchTickerId> {

    List<WatchTicker> findByOwnerOrderBySymbolAsc(String owner);

    boolean existsByOwnerAndSymbol(String owner, String symbol);
}
