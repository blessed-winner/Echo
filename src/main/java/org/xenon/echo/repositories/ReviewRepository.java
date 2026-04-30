package org.xenon.echo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.echo.entities.Review;

public interface ReviewRepository extends JpaRepository<Review,Long> {
}
