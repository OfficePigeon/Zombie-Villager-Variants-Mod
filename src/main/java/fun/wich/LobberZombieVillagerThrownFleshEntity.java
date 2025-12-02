package fun.wich;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class LobberZombieVillagerThrownFleshEntity extends ThrownItemEntity {
	public LobberZombieVillagerThrownFleshEntity(EntityType<? extends LobberZombieVillagerThrownFleshEntity> entityType, World world) { super(entityType, world); }
	public LobberZombieVillagerThrownFleshEntity(World world, LivingEntity owner) { super(ZombieVillagerVariants.LOBBER_ZOMBIE_VILLAGER_THROWN_FLESH, owner, world); }
	@Override protected Item getDefaultItem() { return Items.ROTTEN_FLESH; }
	protected ParticleEffect getParticleParameters() { return new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(getDefaultItem())); }
	@Override
	public void handleStatus(byte status) {
		if (status == 3) {
			ParticleEffect particleEffect = this.getParticleParameters();
			for (int i = 0; i < 8; ++i) {
				this.getEntityWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
			}
		}
	}
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		entityHitResult.getEntity().damage(this.getDamageSources().thrown(this, this.getOwner()), 1);
	}
	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getEntityWorld().isClient()) {
			this.getEntityWorld().sendEntityStatus(this, (byte)3);
			this.discard();
		}
	}
}