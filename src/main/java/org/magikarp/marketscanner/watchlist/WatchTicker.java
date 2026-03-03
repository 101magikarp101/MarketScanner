package org.magikarp.marketscanner.watchlist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "watch_list")
@IdClass(WatchTickerId.class)
@Getter
@Setter
public class WatchTicker {

    @Id
    private String symbol;

    @Id
    private String owner;

    @Column(name = "long_name")
    private String longName;

    @Column(name = "full_exchange_name")
    private String fullExchangeName;

    @CreationTimestamp
    @Column(name = "add_date")
    private LocalDate addDate;

    private String note;
}
