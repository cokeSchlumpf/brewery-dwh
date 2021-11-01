package systems.experiments.model;

import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.List;

@With
@Value
public class Experiment {

    String brewer;

    String description;

    Instant start;

    Instant end;

    Instant mashingStart;

    Instant mashingRest01;

    int mashingRest01Duration;

    Instant mashingRest02;

    int mashingRest02Duration;

    Instant mashingRest03;

    int mashingRest03Duration;

    Instant mashingRest04;

    int mashingRest04Duration;

    Instant mashingEnd;

    Instant boilingStart;

    Instant hop01Adding;

    Instant hop02Adding;

    Instant hop03Adding;

    Instant boilingEnd;

    Instant yeastAdding;

    Instant originalGravity;

    Instant finalGravity;

    List<Ingredient> ingredients;

}
