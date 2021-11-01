package systems.experiments;

import systems.experiments.model.Experiment;

import java.time.Instant;

public interface ProductDevelopmentSystem {

    /**
     * Logs an experiment and returns the experiment id.
     *
     * @param experiment The data of observed during the experiment.
     * @return The experiment id.
     */
    int logExperiment(Experiment experiment);

    /**
     * Submit the rating for an internally rated beer.
     *
     * @param brewer The name of the brewer who created the beer.
     * @param experiment The start date of the experiment.
     * @param ratedBy The name of the guy who rated the beer.
     * @param rating The rating (1 (not so good) .. 10 (oh hell, this is heaven!))
     */
    void rateExperiment(String brewer, Instant experiment, String ratedBy, int rating);

    /**
     * Start planning a new external rating event.
     *
     * @param month The month of the rating event.
     * @param year The year.
     */
    void planNewExternalRatingEvent(int month, int year);

    /**
     * Log results of a rating event.
     *
     * @param month The month of the rating event.
     * @param year The year of the rating event.
     * @param candidate The number of the candidate beer.
     * @param remaining Litres remaining for the candidate beer.
     * @param emptyAt The time when the beer barrel was empty during the event.
     */
    void externalRatingEventResult(int month, int year, int candidate, int remaining, Instant emptyAt);

}
