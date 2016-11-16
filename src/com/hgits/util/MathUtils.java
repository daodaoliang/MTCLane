/**
 * 
 */
package com.hgits.util;

/**
 * 数学运算工具类
 * @author wh
 * @date 2015-05-13
 *
 */
public class MathUtils {

	/**
	 * 一元一次函数
	 * 
	 * @param x1
	 *            默认X1点
	 * @param y1
	 *            默认Y1点
	 * @param x2
	 *            默认X2点
	 * @param y2
	 *            默认Y2点
	 * @param x3
	 *            需要计算的X3
	 * @return 计算后的Y3值
	 */
	public static Double linearFunction(Double x1, Double y1, Double x2, Double y2,
			Double x3) {

		Double times = 1.0;

		/**
		 * 线形递增计算公式 Ty = { T1 * ( R2 - Rx ) + T2 * ( Rx – R1) } / ( R2 – R1 )
		 */
		double d1 = DoubleUtils.mul(y1, DoubleUtils.sub(x2, x3));
		double d2 = DoubleUtils.mul(y2, DoubleUtils.sub(x3, x1));
		double d3 = DoubleUtils.sum(d1, d2);
		double d4 = DoubleUtils.sub(x2, x1);
		times = DoubleUtils.div(d3, d4, 10);

		return times;
	}
}
