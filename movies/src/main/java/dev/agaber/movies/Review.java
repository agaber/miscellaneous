package dev.agaber.movies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "reviews")
public class Review {
  private ObjectId id;
  private String body;
  private LocalDateTime created;
  private LocalDateTime updated;

  public Review(String body, LocalDateTime created, LocalDateTime updated) {
    this.body = body;
    this.created = created;
    this.updated = updated;
  }
}



