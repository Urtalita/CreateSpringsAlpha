package net.Portality.createsprings.utill;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleHelper {
    private static final RandomSource random = RandomSource.create();

    public static void spawnRadialParticles(Level level,
                                            double centerX,
                                            double centerY,
                                            double centerZ,
                                            ParticleOptions particleType,
                                            int count,
                                            double radius,
                                            double speed,
                                            double gravity) {
        if (level.isClientSide()) {
            for (int i = 0; i < count; i++) {
                // Генерация случайного направления
                double theta = random.nextDouble() * 2 * Math.PI;
                double phi = random.nextDouble() * Math.PI;

                // Преобразование сферических координат в декартовы
                double dirX = Math.sin(phi) * Math.cos(theta);
                double dirY = Math.cos(phi);
                double dirZ = Math.sin(phi) * Math.sin(theta);

                // Случайное смещение от центра
                double offset = radius * random.nextDouble();
                double posX = centerX + dirX * offset;
                double posY = centerY + dirY * offset;
                double posZ = centerZ + dirZ * offset;

                // Расчет скорости с учетом гравитации
                double motionX = dirX * speed;
                double motionY = dirY * speed;
                double motionZ = dirZ * speed;

                // Добавление гравитации
                motionY -= gravity * random.nextDouble();

                // Создание частицы
                level.addParticle(particleType, posX, posY, posZ, motionX, motionY, motionZ);
            }
        }
    }

    public static void spawnParticles(Vec3 PartPos, ItemParticleOption data, int amout, Level level){
        spawnRadialParticles(
                level,
                PartPos.x + 0.5, PartPos.y + 0.5, PartPos.z + 0.5,
                data, // Тип частиц
                amout, // Количество
                1.0,                 // Радиус
                0.1,                 // Скорость
                0.05                 // Гравитация
        );
    }
}
