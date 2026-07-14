package br.com.omnirent.item.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_images")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemImage {

    @Id
    private UUID id;

    private String storageKey;

    private Integer displayOrder;

    private Instant createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, insertable=false, updatable=false)
    private Item item;
    
    @Column(name = "item_id")
    private String itemId;
}
