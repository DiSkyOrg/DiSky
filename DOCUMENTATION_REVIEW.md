# Documentation Review Report — DiSky

---

## Méthodologie

Analyse exhaustive de tous les éléments Skript enregistrés dans le projet (effets, expressions, conditions, sections, events) à travers leur symboles de registration. Vérification de la présence et de l'exactitude de `@Name`, `@Description`, `@Examples`, `@Since`.

---

## Fichiers Modifiés

### Bugs Critiques (corrompaient le type system ou le comportement)

---

#### `elements/getters/GetUser.java`
**Problème :** Le second argument de `register()` était `TextChannel.class` au lieu de `User.class`. Cela corrompait le système de types de Skript, enregistrant l'expression comme retournant un `TextChannel` alors qu'elle retourne un `User`.  
**Correction :** `TextChannel.class` → `User.class`

---

#### `elements/effects/retrieve/RetrieveMessage.java`
**Problème :** Les annotations `@Name` et `@Description` étaient importées depuis `jdk.jfr` (Java Flight Recorder) au lieu de `ch.njol.skript.doc`. Résultat : le générateur de documentation Skript ne les voyait pas du tout.  
**Correction :** `import jdk.jfr.Description/Name` → `import ch.njol.skript.doc.Description/Name`

---

#### `elements/datastructs/structures/ButtonStructure.java`
**Problème :** L'annotation `@Name` était importée depuis `jdk.jfr.Name` au lieu de `ch.njol.skript.doc.Name`. Le nom de la structure était invisible au système de docs Skript.  
**Correction :** `import jdk.jfr.Name` → `import ch.njol.skript.doc.Name`

---

#### `elements/sections/automod/AutomodResponse.java`
**Problème :** La méthode `getPropertyName()` retournait `"attachments"` (copié-collé d'un autre fichier) au lieu de `"responses"`. Cela produisait un `toString()` incorrect pour cette expression de propriété.  
**Correction :** `return "attachments"` → `return "responses"`  
**Ajout :** Documentation complète (`@Name`, `@Description`, `@Examples`, `@Since`) ajoutée (était totalement absent).

---

### Typos dans les Patterns (cassaient la syntaxe Skript)

---

#### `elements/events/rework/ChannelEvents.java`
**Problème 1 :** Pattern `"[chaannel] position"` avec double `a` — le mot-clé `channel` était mal orthographié dans le pattern de l'expression timed, rendant `past channel position` / `current channel position` inaccessibles avec la syntaxe documentée.  
**Correction :** `"[chaannel] position"` → `"[channel] position"`

**Problème 2 :** Pattern de l'événement Archive Timestamp `"(timestam|date)"` manquait le `p` final — l'exemple documenté `on channel archive timestamp change` ne correspondait pas au pattern `timestam`.  
**Correction :** `"(timestam|date)"` → `"(timestamp|date)"`

---

### Conflit de Patterns (deux éléments avec patterns identiques)

---

#### `elements/events/rework/GuildVoiceEvents.java`
**Problème :** `GuildVoiceGuildMuteEvent` et `GuildVoiceMuteEvent` étaient tous deux enregistrés avec le nom `"Guild Voice Mute Event"` et le pattern `"[discord] guild [voice] mute[d]"`. Ces deux JDA events sont distincts (server mute vs tout type de mute) mais avaient des patterns identiques causant un conflit de registration.  
**Correction :**  
- `GuildVoiceGuildMuteEvent` → renommé **"Guild Voice Server Mute Event"** avec pattern `"[discord] guild [voice] server mute[d]"` et description mise à jour  
- `GuildVoiceMuteEvent` → conserve le nom/pattern, description mise à jour pour clarifier la différence

---

### Exemples Incorrects (ne correspondaient pas aux patterns ou aux valeurs disponibles)

---

#### `elements/events/rework/GuildEmojiEvents.java`
**Problème :** Les exemples des 4 événements emoji étaient copiés-collés depuis `GuildStickerEvents.java`. Ils utilisaient les patterns d'événements sticker (`on guild sticker add`) et les valeurs sticker (`%event-sticker%`) au lieu des valeurs emoji.  
**Correction :** Tous les 4 exemples corrigés pour utiliser les patterns et valeurs emoji corrects :
- `on emoji added` / `event-emote`
- `on emoji removed` / `event-emote`
- `on emoji roles updated` / `event-emote`
- `on emoji name updated` / `event-emote` + `%old name%` / `%new name%`

---

#### `elements/events/rework/ComponentEvents.java`
**Problème 1 :** Description du Button Click : `"Fired when any button sent by the button is clicked"` — "by the button" était une copie erronée, doit être "by the bot".  
**Correction :** `"sent by the button"` → `"sent by the bot"`

**Problème 2 :** L'exemple du Modal Received disait `"You clicked the button with id '%received modal%'!"` — confondait un modal avec un bouton.  
**Correction :** Exemple mis à jour : `"You submitted the modal with id '%received modal%'!"`

---

#### `elements/events/rework/GuildStickerEvents.java`
**Problème :** L'exemple du Guild Sticker Remove utilisait `%event-user%` mais aucun `User.class` n'est enregistré comme valeur pour cet événement.  
**Correction :** Exemple reécrit sans `event-user` : `"Sticker %event-sticker% was removed from %event-guild%"`

---

#### `elements/getters/GetSticker.java`
**Problème :** L'exemple `"sticker with named \"meliodas\""` ne correspond pas au pattern `[get] [the] sticker (with name|named) %string%` — `with named` n'est pas une alternative valide.  
**Correction :** `"sticker with named \"meliodas\""` → `"sticker named \"meliodas\""`

---

#### `elements/effects/PublishMessage.java`
**Problème :** Le second exemple `crosspost message with id "000" in channel with id "123"` suggérait une récupération inline d'un message par ID, qui n'est pas possible avec la syntaxe de cet effet (qui prend un `%message%` objet déjà résolu).  
**Correction :** `"crosspost message with id \"000\" in channel with id \"123\""` → `"crosspost {_msg}"`

---

#### `elements/events/rework/GuildInviteEvents.java`
**Problème :** La description du "Invite Delete Event" affirmait qu'on pouvait accéder au "invite code", mais aucun `Invite.class` n'est enregistré comme valeur pour cet événement.  
**Correction :** Description corrigée pour ne mentionner que `channel` et `guild`.

---

### Mauvais Placement d'Annotations

---

#### `elements/getters/GetTimeBoosted.java`
**Problème :** `"This expression cannot be changed."` était placé à l'intérieur de l'annotation `@Examples` comme si c'était un exemple de code.  
**Correction :** Déplacé dans `@Description` ; `@Examples` ne contient maintenant que des exemples de code valides.

---

### Descriptions Inexactes

---

#### `elements/getters/GetGuild.java`
**Problème :** `"Get a guild from a guild using its unique ID"` — tautologie évidente (copié-collé).  
**Correction :** `"Get a guild from a guild"` → `"Get a guild"`

---

#### `elements/getters/GetGuildChannel.java`
**Problème 1 :** `@Name("Get Channel")` dupliquait exactement le nom de `GetChannel.java`.  
**Correction :** `@Name("Get Channel")` → `@Name("Get Guild Channel")`

**Problème 2 :** `getCodeName()` retournait `"channel"` au lieu de `"guild channel"`, produisant un `toString()` incorrect.  
**Correction :** `return "channel"` → `return "guild channel"`

**Problème 3 :** Description `"Get a channel from a guild"` (lookup est depuis le cache bot, pas d'une guild).  
**Correction :** `"Get a channel from a guild"` → `"Get a guild channel"`

---

#### `elements/conditions/MessageOrigin.java`
**Problème :** Description contenait `"Check either a message(related event come from a guild"` — faute de grammaire (`either` au lieu de `whether`, parenthèse non fermée, `come` au lieu de `comes`).  
**Correction :** `"Check either a message(related event come from..."` → `"Check whether a message-related event comes from..."`

---

### Corrections toString()

---

#### `elements/effects/CreateThread.java`
**Problème :** `toString()` retournait `"create new role named ..."` — copié-collé depuis CreateRole.  
**Correction :** `"create new role named"` → `"create new thread named"`

---

#### `elements/commands/values/UsedPrefix.java`
**Problème :** `toString()` retournait `"the used alias"` — copié-collé depuis `UsedAlias.java`.  
**Correction :** `"the used alias"` → `"the used prefix"`  
**Ajout :** `@Since("4.0.0")` manquant ajouté.

---

### Documentation Manquante — Ajout Complet

Les fichiers suivants étaient totalement non documentés. Toutes les annotations requises ont été ajoutées (`@Name`, `@Description`, `@Examples`, `@Since`).

#### `elements/effects/retrieve/`
| Fichier | @Name ajouté |
|---|---|
| `RetrieveApplicationInfo.java` | "Retrieve Application Info" |
| `RetrieveBans.java` | "Retrieve Bans" |
| `RetrieveEmotes.java` | "Retrieve Emotes" |
| `RetrieveInvite.java` | "Retrieve Invite" |
| `RetrieveInvites.java` | "Retrieve Invites" |
| `RetrieveMember.java` | "Retrieve Member" |
| `RetrieveOwner.java` | "Retrieve Owner" |
| `RetrievePinnedMessages.java` | "Retrieve Pinned Messages" |
| `RetrieveStartMessage.java` | "Retrieve Thread Start Message" |
| `RetrieveThreadMessage.java` | "Retrieve Thread Message" |
| `RetrieveUser.java` | "Retrieve User" |
| `RetrieveWebhooks.java` | "Retrieve Webhooks" |

#### `elements/effects/retrieve/` — Documentation Partielle Complétée
| Fichier | Annotations ajoutées |
|---|---|
| `RetrieveInterestedMembers.java` | `@Examples`, `@Since` |
| `RetrieveMessages.java` | `@Since` |
| `RetrieveProfile.java` | `@Examples`, `@Since` |
| `RetrieveSticker.java` | `@Examples`, `@Since` |
| `RetrieveStickers.java` | `@Examples`, `@Since` |
| `RetrieveThreadMembers.java` | `@Examples`, `@Since` |
| `RetrieveThreads.java` | `@Examples`, `@Since` |

#### `elements/effects/webhooks/`
| Fichier | @Name ajouté |
|---|---|
| `RegisterClient.java` | "Register Webhook Client" |
| `UnregisterClient.java` | "Unregister Webhook Client" |
| `MakeClientSpeak.java` | "Post Webhook Message" |

#### `elements/effects/find/`
| Fichier | @Name ajouté |
|---|---|
| `FindMembersWithNickname.java` | "Find Members With Nickname" |

#### `elements/events/members/`
| Fichier | @Name ajouté |
|---|---|
| `MemberBanEvent.java` | "Member Ban Event" |
| `MemberKickEvent.java` | "Member Kick Event" |

#### `elements/sections/handler/`
| Fichier | @Name ajouté |
|---|---|
| `SecTry.java` | "Try/Catch Section" |

#### `elements/commands/`
| Fichier | Annotations ajoutées |
|---|---|
| `ExprArgument.java` | `@Examples`, `@Since` (+ amélioration description) |
| `values/UsedArgument.java` | `@Examples`, `@Since` (+ réécriture description) |
| `values/UsedAlias.java` | (vérifié — OK) |

#### `elements/sections/automod/`
| Fichier | @Name ajouté |
|---|---|
| `AutomodResponse.java` | "AutoMod Rule Responses" (+ fix `getPropertyName`) |

---

---

## Fichiers Modifiés — `elements/properties/`

*(Analyse réalisée par agent séparé, corrections appliquées)*

### Bugs de getPropertyName() / toString() (impactaient le debug output)

| Fichier | Valeur incorrecte | Valeur corrigée |
|---|---|---|
| `channels/ChannelNSFW.java` | `toString()` = `"channel topic"` | `"channel nsfw"` |
| `channels/ChannelMembers.java` | `getPropertyName()` = `"threads"` | `"voice members"` |
| `messages/MessageMentionedUsers.java` | `getPropertyName()` = `"mentioned uers"` | `"mentioned users"` |
| `messages/MessageMentionedVoiceChannels.java` | `getPropertyName()` = `"mentioned channels"` | `"mentioned voice channels"` |
| `role/colors/TertiaryRoleColor.java` | `getPropertyName()` = `"secondary role color"` | `"tertiary role color"` |
| `reactions/ReactionIsSelf.java` | `getPropertyName()` = `"super emoji"` | `"self emoji"` |
| `embeds/EmbedToJSON.java` | `toString()` = `"embed from json ..."` | `"embed ... to json"` |

### Descriptions Incorrectes dans Properties

| Fichier | Problème | Correction |
|---|---|---|
| `role/colors/SecondaryRoleColor.java` | Description disait "Returns the **primary** color" | Corrigé en "secondary" |
| `forums/DefaultEmoji.java` | Typo `"mote"` au lieu de `"emote"` | Corrigé |

### Exemples ne Correspondant pas aux Patterns

| Fichier | Problème | Correction |
|---|---|---|
| `DiscordMembersOf.java` | 2 premiers exemples omettaient le préfixe obligatoire `discord` | Ajout de `discord members of ...` |
| `emotes/EmoteURL.java` | `"emote url of event-emote"` ne matchait pas le pattern `emo(te\|ji) image [url]` | Corrigé en `"emote image url of event-emote"` |
| `attachments/CondIsImage.java` | Exemple `loop-value is an image` ne matchait pas le pattern (doit avoir `att[achment[s]] %attachment%` prefix) | *Non corrigé — nécessite refactoring du pattern* |
| `attachments/CondIsSpoiler.java` | Même problème | *Non corrigé* |
| `attachments/CondIsVideo.java` | Même problème | *Non corrigé* |

### Documentation Ajoutée dans Properties

| Fichier | Annotations ajoutées |
|---|---|
| `reactions/ReactionIsSelf.java` | Toutes (était complètement non documentée) |
| `embeds/EmbedToJSON.java` | Toutes (était complètement non documentée) |

---

## Problèmes Identifiés Mais Non Corrigés

Ces problèmes nécessitent une décision de design ou un refactoring plus important.

### Conflit de Patterns entre deux fichiers

**`elements/effects/retrieve/RetrieveWebhooks.java`** (dans `retrieve/`) et **`elements/effects/webhooks/RetrieveWebhooks.java`** (dans `webhooks/`) registrent deux patterns qui se chevauchent :
- `retrieve [all] [discord] webhooks (of|from) [the] [(guild|channel)] %guild/textchannel% ...`
- `retrieve [the] webhook[s] (from|of) [the] [channel] %channel/textchannel/guild% ...`

Les deux matcheront `retrieve webhooks from event-guild`. Une des deux implémentations devrait être supprimée ou les patterns différenciés clairement.

### Expression Silencieusement Cassée

**`elements/getters/LastDiSkyException.java`** — La méthode `get()` retourne toujours un tableau vide et contient un commentaire `TODO`. L'expression est non-fonctionnelle après la migration vers `DiSkyRuntimeHandler`. La description affirme qu'elle fonctionne normalement. À corriger ou supprimer.

### Bug Potentiel — Paramètre Ignoré

**`elements/effects/retrieve/RetrieveProfile.java`** — Le paramètre `%string%` (ID) dans le pattern est parsé et stocké dans `exprID` mais n'est jamais utilisé dans `execute()` — l'implémentation appelle `user.retrieveProfile()` sans ID. Le paramètre ID est vestigial.

### Section Désactivée mais Enregistrée

**`elements/sections/once/SecListenOnce.java`** — La méthode `init()` contient `if (true) { Skript.warning(...); return false; }` qui désactive systématiquement toute utilisation. L'élément est enregistré (visible dans Skript) mais rejette toujours toute utilisation. Il faudrait soit le retirer de la registration, soit le réactiver.

### Structures Centrales Non Documentées

Les structures suivantes restent non documentées (elles utilisent `Skript.registerStructure()` sans système de builder supportant les annotations) :
- **`elements/structures/StructBot.java`** — La structure `define bot` (la plus importante du plugin)
- **`elements/structures/slash/StructSlashCommand.java`** — La structure `slash command`
- **`elements/structures/context/StructContextCommand.java`** — Les context commands

Ces éléments nécessitent l'ajout de commentaires de documentation au niveau de `Skript.registerStructure()` ou d'une migration vers un système supportant les annotations.

### Elements de Slash Commands sans Documentation

- **`elements/structures/slash/elements/ExprSlashArgument.java`** — Accès aux arguments dans les slash commands (pattern identique à `ExprArgument.java`, contexte différent)
- **`elements/structures/slash/elements/ExprRemainingTime.java`** — Temps restant du cooldown

### Automod — Documentation Partielle

Les éléments suivants restent non documentés :
- `elements/sections/automod/AutomodType.java`
- `elements/sections/automod/CreateAutoMod.java`
- `elements/sections/automod/CreateRule.java`
- `elements/sections/automod/NewRuleResponse.java`
- `elements/sections/automod/NewRuleType.java`

### Data Structures sans Exemples

- **`elements/datastructs/structures/EmbedStructure.java`** — Manque `@Examples`
- **`elements/datastructs/RoleDS.java`** — Aucune documentation

### @Since Universellement Absent des Events Rework

Le système `EventRegistryFactory` (utilisé pour tous les events dans `events/rework/`) n'expose pas de méthode `.since()`. Aucun des événements rework n'a de `@Since`. Si la documentation l'exige, il faudrait ajouter le support `.since()` au builder.

---

## Résumé des Changements

| Catégorie | Nombre de fichiers |
|---|---|
| Bugs critiques (type system, imports) | 4 |
| Typos dans les patterns | 2 fichiers, 2 corrections |
| Conflit de patterns résolu (GuildVoiceMute) | 1 |
| Exemples incorrects corrigés (effects/events) | 6 |
| Descriptions inexactes corrigées (effects/events) | 5 |
| toString() / getPropertyName() corrigés | 9 |
| Documentation complète ajoutée (effects/events) | 22 |
| Documentation partielle complétée (effects) | 9 |
| Documentation ajoutée (properties) | 2 |
| Exemples incorrects corrigés (properties) | 2 |
| Descriptions incorrectes corrigées (properties) | 2 |
| **Total fichiers modifiés** | **~52** |
