package org.example.demo_datn.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Photo extends Base{
    private String imageUrl;
    private String thumbnailUrl;
    private long  size;
    private int width;
    private int height;

    @ManyToOne
    private Album album;

    @ManyToOne
    private User owner;
}
