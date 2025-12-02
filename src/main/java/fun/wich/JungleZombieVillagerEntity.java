package fun.wich;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class JungleZombieVillagerEntity extends ExtendedZombieVillagerEntity implements Shearable {
	public JungleZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) { super(entityType, world); }
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
			if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
				this.sheared(serverWorld, SoundCategory.PLAYERS, itemStack);
				this.emitGameEvent(GameEvent.SHEAR, player);
				itemStack.damage(1, player, hand.getEquipmentSlot());
			}
			return ActionResult.SUCCESS;
		}
		else return super.interactMob(player, hand);
	}
	public void sheared(ServerWorld world, SoundCategory shearedSoundCategory, ItemStack shears) {
		world.playSoundFromEntity(null, this, ZombieVillagerVariants.ENTITY_JUNGLE_ZOMBIE_VILLAGER_SHEAR, shearedSoundCategory, 1, 1);
		ConvertToZombieVillagerEntity(this, EntityType.ZOMBIE_VILLAGER, ZombieVillagerVariants.ENTITY_JUNGLE_ZOMBIE_VILLAGER_SHEAR);
		this.forEachShearedItem(world, ZombieVillagerVariants.JUNGLE_ZOMBIE_VILLAGER_SHEARING, shears, (worldx, stack) -> {
			for (int i = 0; i < stack.getCount(); ++i) {
				worldx.spawnEntity(new ItemEntity(this.getEntityWorld(), this.getX(), this.getBodyY(1), this.getZ(), stack.copyWithCount(1)));
			}
		});
	}
	public boolean isShearable() { return this.isAlive() && !this.isBaby(); }
	@Override
	public SoundEvent GetCureSound() { return ZombieVillagerVariants.ENTITY_JUNGLE_ZOMBIE_VILLAGER_CURE; }
	@Override
	public boolean burnsInDaylight() { return false; }
	@Override
	public SoundEvent getAmbientSound() { return ZombieVillagerVariants.ENTITY_JUNGLE_ZOMBIE_VILLAGER_AMBIENT; }
	@Override
	public SoundEvent getHurtSound(DamageSource source) { return ZombieVillagerVariants.ENTITY_JUNGLE_ZOMBIE_VILLAGER_HURT; }
	@Override
	public SoundEvent getDeathSound() { return ZombieVillagerVariants.ENTITY_JUNGLE_ZOMBIE_VILLAGER_DEATH; }
	@Override
	public SoundEvent getStepSound() { return ZombieVillagerVariants.ENTITY_JUNGLE_ZOMBIE_VILLAGER_STEP; }
	@Override
	public RegistryEntry<StatusEffect> GetStatusEffectOnHit() { return StatusEffects.POISON; }
}