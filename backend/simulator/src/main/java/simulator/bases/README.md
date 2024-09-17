# bases

All the bases are just template needs to be extended and implemented in the way you want the strategy to work.

## Table of Contents 📚

- [StrategyBase 📈](#strategybase-📈)
- [StrategyRunnerBase 🏛️](#strategyrunnerbase-🏛️)
- [IndicatorBase 🗠](#indicatorbase-🗠)

## StrategyBase 📈

This is the platform to create a strategy. Extend this class and implement the unimplemented methods and you will be able to do a simulation with the `StrategyRunnerBase`.

## StrategyRunnerBase 🏛️

This is a class that handle all fetch data from database and write back. It generate the strategys start the simulation. The simulation is runned in a main methode.

## IndicatorBase 🗠

The indicators that extends IndicatorBase can both be used on `Symbol` as well as `ResultService`. This means it's possible to use it inside a strategy on a `Symbol` or on a simulated result (stored in `ResultService`).

The idè for the last one is that a simulation can be runned inside a simulation, and the outher simulation can generate signals with a indicator based on the inner simulation. Example is that you want to turn off the strategy when a drawdown greather than 10%. To do this you can run a simulation inside the main simulation and allow unlimited drawdown. Then you can create a drawdown indicator and apply on the result you get from the inner simulation. Then you can use the indicator like this: `drawdownIndicator.getValue(date)`. (This is just a example, the _drawdownIndicator_ need to be defined)
