package net.yastral.theendisheremod.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.resources.ResourceLocation;
import net.yastral.theendisheremod.TheEndIsHereMod;

public class ShadowSkinManager {

    /**
     * Applica una skin completamente nera al profilo del giocatore
     * @param profile Il GameProfile a cui applicare la skin nera
     */
    public static void applyShadowSkin(GameProfile profile) {
        // Il valore della skin è una stringa Base64 che rappresenta la texture
        // Questa texture è personalizzata per essere completamente nera
        String skinValue = "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=";
        String skinSignature = "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKbhIWmpYWkog9voz6M9lzj+6T4s1fzRSgLGJJp2wV5RiNiDz45ZR045lhjyt46AiaDYvT/tsD9Mz0NKJ6G9n/eMYxjBQmgCpVCK8Af9ukJCHj67JyVXo//7iWJaHNFmDT/HubBvz8K9/uvlXBtMfTXueiPTFIKDsCKZ7+9j1J7uUZ/KKFxFOamLupjzptzHNqH3+wwxdDXR63khB8gLPOOu3t6EYjtPLhVyRLBWOPjjJZ5+1LEjCJGAqm0/U5KRPoZsxLMlpGLla/9FsGmJW+9EdP4ND2X5i25n1a9XEH7QKCidrF4evgbYJHx5pnTjzwu2kgYdZxFI9VgzHMime2W8nR29t3hIK8ICAvxQvMhnFxXw2zmjJhznpGjKA8d3sJmK5tddd0rb3aOq4/TCQoKq2RxRMT/+2fH05E9FdU8x5c3KIzMN+MTR3JJVkPg4d4eq6KjWNWwA2vaEdOx/yAHj5uo4SG9a0h7dWrS/gj8EkKcGnvSOXEcU3O7r4VEHEHEVaESdI=";

        // Aggiungiamo le proprietà della skin al profilo
        profile.getProperties().put("textures", new Property("textures", skinValue, skinSignature));
    }

    /**
     * Restituisce il percorso della texture della skin nera
     * @return ResourceLocation per la skin nera
     */
    public static ResourceLocation getShadowSkinTexture() {
        return new ResourceLocation(TheEndIsHereMod.MOD_ID, "textures/entity/shadow_player.png");
    }
}
