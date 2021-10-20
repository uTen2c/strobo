package dev.uten2c.strobo.util

import net.minecraft.util.math.Vec3d

/**
 * ベクトルの足し算
 * @param vec3d 足すベクトル
 * @return 計算後の[Vec3d]
 */
operator fun Vec3d.plus(vec3d: Vec3d): Vec3d = this.add(vec3d)

/**
 * ベクトルの引き算
 * @param vec3d 引くベクトル
 * @return 計算後の[Vec3d]
 */
operator fun Vec3d.minus(vec3d: Vec3d): Vec3d = this.subtract(vec3d)

/**
 * ベクトルの掛け算
 * @param vec3d 掛けるベクトル
 * @return 計算後の[Vec3d]
 */
operator fun Vec3d.times(vec3d: Vec3d): Vec3d = this.multiply(vec3d)

/**
 * ベクトルの掛け算
 * @param number 掛ける数
 * @return 計算後の[Vec3d]
 */
operator fun Vec3d.times(number: Number): Vec3d = this.multiply(number.toDouble())
