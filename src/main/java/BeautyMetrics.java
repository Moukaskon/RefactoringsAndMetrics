package main.java;

public class BeautyMetrics {

    private double balance;
    private double equilibrium;
    private double density;
    private double regularity;
    private double rhythm;
    private double sequence;
    private double simplicity;
    private double symmetry;

    public BeautyMetrics(
            double balance,
            double equilibrium,
            double density,
            double regularity,
            double rhythm,
            double sequence,
            double simplicity,
            double symmetry
    ) {
        this.balance = balance;
        this.equilibrium = equilibrium;
        this.density = density;
        this.regularity = regularity;
        this.rhythm = rhythm;
        this.sequence = sequence;
        this.simplicity = simplicity;
        this.symmetry = symmetry;
    }

    public double getBalance() {
        return balance;
    }

    public double getEquilibrium() {
        return equilibrium;
    }

    public double getDensity() {
        return density;
    }

    public double getRegularity() {
        return regularity;
    }

    public double getRhythm() {
        return rhythm;
    }

    public double getSequence() {
        return sequence;
    }

    public double getSimplicity() {
        return simplicity;
    }

    public double getSymmetry() {
        return symmetry;
    }


}
