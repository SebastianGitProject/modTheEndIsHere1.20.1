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
        String skinValue = "ewogICJ0aW1lc3RhbXAiIDogMTYyMTE4MzM5NzEwOSwKICAicHJvZmlsZUlkIiA6ICI5ZDIyZGRhOTVmZGI0MjFmOGZhNjAzNTI1YThkZmE4ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTYWZlRHJpZnQ0OCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yYTNiODY2OGYyMjk1NDJjNGU1MWE3MWQ4NWJjMjE1ZjI3OTAyMzA4YmRkYWZjN2M5YjBkODRmZTFjZmYyOWI3IgogICAgfQogIH0KfQ==";
        String skinSignature = "ZPalL36ZbCUa0f5ICLBdLxKhbPf/gt3B39H9OdBSflxk0sif2aaNapgQ6xkN34O2VSTHGHzAlCzn1gJZrZoFjqY8+GV7eraz2dvIvzb2mg1q0vgI+M5Kyhj8J7gM4Uiu8aXRHqF8YqNHFrtA4H2meu0jVFR+mD7Sx3dQSr/DSex/H4AiPErRrXv1x6OngYvxEPwp2Hr+dEKo4iAFv93qpXEFqDmfLvqFgF60ql5/wjsAgD8IE51sh/ZE5U/FSUQw9B8tz3JosnK4LCtAtP7VENk2cfA1hOqMTX7bIdBAFVyU0jrjp5UuLywi8fpp+5m+DPgg/p9B88qHdj3g6TmmhzuZkmUEYOnkp/NoF19zNwgBymIgZjNzdJZn6UGp6e3sGLJaYmGpjS7efVAuazl71ytU/l55VSD6PZ5Jm2mKW9osfJXqQsf0+jDsL0znOK9Vlkg0vDt/0Dd69NoTqvJwvlJ5aHSk2hyyCvIkX5Iv82F5fSZjQdug3robKuDOdS9abECKoZo7Eci0BCFMUhfcuJHCu8vb0eLbp7SijuATzDt6PLPd4O+9Bqyp9Xd7PHl9vPiS7F9qbwEhsKP206gKsZOcMV2/fo0Uq6mUB2pXvCpzyymeBovl7qlxZk275dQ7SEBlVpLlhELfsTtIxxT2UfQstvF7V5qu+GY8UmSn0uU=";

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
