import * as ss from 'simple-statistics';

export function calculatePearsonCorrelation(list1: number[], list2: number[]): number {
    return ss.sampleCorrelation(list1, list2);
}

export function calculateBeta(stockReturns: number[], marketReturns: number[]): number {
    const covariance = ss.sampleCovariance(stockReturns, marketReturns);
    const variance = ss.variance(marketReturns);
    return covariance / variance;
}
