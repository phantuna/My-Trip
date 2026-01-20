package org.example.demo_datn.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_datn.Enum.AlbumStatus;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Album extends Base{
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private AlbumStatus status;

    @ManyToOne
    private Locations location;
    @ManyToOne
    private User owner;

}
