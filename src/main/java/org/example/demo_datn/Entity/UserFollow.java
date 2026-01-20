package org.example.demo_datn.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class UserFollow extends Base {

    @ManyToOne
    private User follower;

    @ManyToOne
    private User following;
}

