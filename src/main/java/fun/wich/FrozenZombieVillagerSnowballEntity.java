package fun.wich;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
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

public class FrozenZombieVillagerSnowballEntity extends ThrownItemEntity {
	public FrozenZombieVillagerSnowballEntity(EntityType<? extends FrozenZombieVillagerSnowballEntity> entityType, World world) { super(entityType, world); }
	public FrozenZombieVillagerSnowballEntity(World world, LivingEntity owner) { super(ZombieVillagerVariants.FROZEN_ZOMBIE_VILLAGER_SNOWBALL, owner, world); }
	@Override
	protected Item getDefaultItem() { return Items.SNOWBALL; }
	private ParticleEffect getParticleParameters() {
		ItemStack itemStack = this.getStack();
		return itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack);
	}
	public void handleStatus(byte status) {
		if (status == 3) {
			ParticleEffect particleEffect = this.getParticleParameters();
			for (int i = 0; i < 8; ++i) {
				this.getEntityWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
			}
		}
	}
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		Entity entity = entityHitResult.getEntity();
		int i = entity instanceof BlazeEntity ? 3 : 0;
		Entity owner = this.getOwner();
		if (entity instanceof LivingEntity living) living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60), owner == null ? this : owner);
		entity.damage(this.getDamageSources().thrown(this, this.getOwner()), (float)i);
	}
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getEntityWorld().isClient()) {
			this.getEntityWorld().sendEntityStatus(this, (byte)3);
			this.discard();
		}
	}
}
