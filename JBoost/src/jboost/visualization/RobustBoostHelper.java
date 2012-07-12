package jboost.visualization;

import org.jfree.data.xy.XYSeries;

import jboost.booster.RobustBoost;

public class RobustBoostHelper {

  public static double calculateRho(double sigma_f, double epsilon, double theta) {

    double f1 = Math.sqrt(Math.exp(2.0) * ((sigma_f * sigma_f) + 1.0) - 1.0);
    double f2 = RobustBoost.erfinv(1.0 - epsilon);
    double numer = (f1 * f2) + Math.E * theta;
    double denom = 2.0 * (Math.E - 1.0);
    return numer / denom;

  }

  public static double calculateWeight(double sigma_f, double epsilon, double theta, double rho, double m, double t) {
    return RobustBoost.calculateWeight(rho, theta, sigma_f, 1.0, m, t);
  }

  public static double calculatePotential(double sigma_f, double epsilon, double theta, double rho, double m, double t) {
    return RobustBoost.calculatePotential(rho, theta, sigma_f, 1.0, m, t);
  }

  public static XYSeries getPosWeightPlot(double sigma_f, double epsilon, double theta, double rho, double t, double height, double min,
                                          double max, double step) {
    XYSeries weight = new XYSeries("pos weights");
    double current = min, w;
    while (current < max) {
      w = calculateWeight(sigma_f, epsilon, theta, rho, current, t) * height;
      weight.add(current, w);
      current += step;
    }
    return weight;
  }

  public static XYSeries getNegWeightPlot(double sigma_f, double epsilon, double theta, double rho, double t, double height, double min,
                                          double max, double step) {
    XYSeries weight = new XYSeries("neg weights");
    double current = -max, w;
    while (current < -min) {
      w = calculateWeight(sigma_f, epsilon, theta, rho, current, t) * height;
      weight.add(-current, w);
      current += step;
    }
    return weight;
  }

  public static XYSeries getPosPotentialPlot(double sigma_f, double epsilon, double theta, double rho, double t, double height, double min,
                                             double max, double step) {
    XYSeries weight = new XYSeries("pos potentials");
    double current = min, w;
    while (current < max) {
      w = calculatePotential(sigma_f, epsilon, theta, rho, current, t) * height;
      weight.add(current, w);
      current += step;
    }
    return weight;
  }

  public static XYSeries getNegPotentialPlot(double sigma_f, double epsilon, double theta, double rho, double t, double height, double min,
                                             double max, double step) {
    XYSeries weight = new XYSeries("neg potentials");
    double current = -max, w;
    while (current < -min) {
      w = calculatePotential(sigma_f, epsilon, theta, rho, current, t) * height;
      weight.add(-current, w);
      current += step;
    }
    return weight;
  }

}
