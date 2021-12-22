package simulation.entities.brewery.values;

import common.P;
import lombok.Value;
import systems.brewery.values.Brew;
import systems.brewery.values.event.IngredientAdded;
import systems.brewery.values.event.Mashed;
import systems.brewery.values.event.Sparged;
import systems.brewery.values.instructions.AddIngredient;

import java.time.Duration;
import java.util.stream.Collectors;

@Value
public class TasteProperties {

    // range from 0 to 1 (e.g. 0 = not sweet at all, 1 = maximum sweetness)
    double alcohol;

    double beer_foam;

    double cloudiness;

    double sparkling;

    public double sweetness;

    public double malty;

    public TasteProperties(Brew brew){
        this.alcohol = determineAlcohol(brew);
        this.beer_foam = determineBeerFoam(brew);
        this.cloudiness = determineCloudiness(brew);
        this.sparkling = determineSparkling(brew);
        this.sweetness = determineSweetness(brew);
        this.malty = determineMaltLevel(brew);
        // einfach überall ein regressionsmodell bauen

    }

    private double determineAlcohol(Brew brew){
        // ABW = (0.372 + 0.00357 * OE) * (OE - AE)
        // ABV = ABW * (1.308 * 10-5 + 3.868 * 10-3 * AE + 1.275 * 10-5 * AE2 + 6.3 * 10-8 * AE3 + 1) / 0.7907
        // https://www.omnicalculator.com/food/alcohol-by-volume#calculating-abv-from-specific-gravity

        var oe = brew.getOriginalGravity();
        var ae = brew.getFinalGravity().get();
        var abw = (0.372 + 0.00357 * oe) *(oe-ae);
        var abv = abw * (1.308 * Math.pow(10,-5.0) + 3.868 * Math.pow(10,-3.0) * ae + 1.275 * Math.pow(10,-5.0) * Math.pow(ae,2.0) + 6.3 * 10-8 * Math.pow(ae,3.0) + 1);
        abv = (abv-0.04)/0.03;

        var result = 1/(1+ Math.exp(-(Math.pow(abv,-0.5)+P.randomDouble(0.0,0.2))));
        return result;
    }

    private double determineBeerFoam(Brew brew){
        return 0.0;
    }

    private double determineCloudiness(Brew brew){
        double result = 0.0;
        if(brew.getBeer().getBeerName()=="foo"){
            result = Math.min(1.0, 0.9+P.randomDouble(0,0.1));
        }

        if(brew.getBeer().getBeerName()=="bar"){
            result = Math.max(0.0, 0.2+P.randomDouble(0,0.1));
        }
        return result;
    }

    private double determineMaltLevel(Brew brew){

        var water = brew.getEvents()
                .stream()
                .filter(brewEvent -> brewEvent instanceof IngredientAdded)
                .filter(ingredient -> ((IngredientAdded) ingredient).getProduct().getIngredient().getName().contains("water"))
                .mapToDouble(water_added -> ((IngredientAdded) water_added).getAmount())
                .sum();


        //amount of malt
        var malt = brew.getEvents()
                .stream()
                .filter(brewEvent -> brewEvent instanceof IngredientAdded)
                .filter(ingredient -> {
                    var ing = ((IngredientAdded) ingredient).getProduct().getIngredient();
                    return (ing.getName().contains("malt"));
                })
                .mapToDouble(water_added -> ((IngredientAdded) water_added).getAmount())
                .sum();

        var malt_ratio = malt/ water;

        // Duration of mashing
        var mashing_minutes = brew.getEvents()
                .stream()
                .filter(brewEvent -> brewEvent instanceof Mashed)
                .map(mashing -> Duration.between(((Sparged) mashing).getStart(), ((Sparged) mashing).getEnd()).toMinutes())
                .collect(Collectors.toList());

        // duration of maching
        var mashing_temperature_increase = brew.getEvents()
                .stream()
                .filter(brewEvent -> brewEvent instanceof Mashed)
                .map(mashing -> ((Mashed) mashing).getEndTemperature()-((Mashed) mashing).getStartTemperature())
                .collect(Collectors.toList());

        var linear_value = 7 * malt/water + P.randomDouble(0,0.9);

        for (int i = 0; i < mashing_minutes.size();i++){
            linear_value = linear_value + 0.2*mashing_minutes.get(i) - 0.3 * mashing_temperature_increase.get(i);
        }
        var result = 1/(1+ Math.exp(-linear_value));
        return result;
    }

    private double determineSparkling(Brew brew){
        return 0.0;
    }

    private double determineSweetness(Brew brew){

        var water = brew.getEvents()
                                            .stream()
                                            .filter(brewEvent -> brewEvent instanceof IngredientAdded)
                                            .filter(ingredient -> ((IngredientAdded) ingredient).getProduct().getIngredient().getName().contains("water"))
                                            .mapToDouble(water_added -> ((IngredientAdded) water_added).getAmount())
                                            .sum();

        if(water==0.0){ throw new RuntimeException("In this brew no water was added");}

        // get value of ingredients
        var fruits = brew.getEvents()
                                    .stream()
                                    .filter(brewEvent -> brewEvent instanceof IngredientAdded)
                                    .filter(ingredient -> {
                                        var ing = ((IngredientAdded) ingredient).getProduct().getIngredient();
                                        return (ing.getName().contains("lemon") || ing.getName().contains("grapefruit"));
                                    })
                                    .mapToDouble(water_added -> ((IngredientAdded) water_added).getAmount())
                                    .sum();

        var sugar = brew.getEvents()
                .stream()
                .filter(brewEvent -> brewEvent instanceof IngredientAdded)
                .filter(ingredient -> {
                    var ing = ((IngredientAdded) ingredient).getProduct().getIngredient();
                    return (ing.getName().contains("sugar") || ing.getName().contains("ose"));
                })
                .mapToDouble(water_added -> ((IngredientAdded) water_added).getAmount())
                .sum();

        var hop = brew.getEvents()
                .stream()
                .filter(brewEvent -> brewEvent instanceof IngredientAdded)
                .filter(ingredient -> {
                    var ing = ((IngredientAdded) ingredient).getProduct().getIngredient();
                    return (ing.getName().contains("hop"));
                })
                .mapToDouble(water_added -> ((IngredientAdded) water_added).getAmount())
                .sum();

        var sparging_time = brew.getEvents()
                .stream()
                .filter(brewEvent -> brewEvent instanceof Sparged)
                .mapToDouble(sparging -> Duration.between(((Sparged) sparging).getStart(), ((Sparged) sparging).getEnd()).toMinutes())
                .findFirst();

        if(sparging_time.isEmpty()){ throw new RuntimeException("No sparging for this brew");}

        var sweetness_ratio = (fruits+2*sugar)/water;
        var final_gravity = brew.getFinalGravity().get().doubleValue();

        var linear_value = -3 * hop + 3*sweetness_ratio + 5*final_gravity+ 0.5*sparging_time.getAsDouble()+ P.randomDouble(0,1.0);

        var result = 1/(1+ Math.exp(-linear_value));

        return result;
    }


}
