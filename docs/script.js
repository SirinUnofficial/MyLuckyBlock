document.getElementById('dropItemsEnabled').addEventListener('change', function() {
    const dropItemsContainer = document.getElementById('dropItemsContainer');
    dropItemsContainer.classList.toggle('hidden', !this.checked);
});

document.getElementById('placeBlocksEnabled').addEventListener('change', function() {
    const placeBlocksContainer = document.getElementById('placeBlocksContainer');
    placeBlocksContainer.classList.toggle('hidden', !this.checked);
});

document.getElementById('placeChestsEnabled').addEventListener('change', function() {
    const placeChestsContainer = document.getElementById('placeChestsContainer');
    placeChestsContainer.classList.toggle('hidden', !this.checked);
});

document.getElementById('fallBlocksEnabled').addEventListener('change', function() {
    const fallBlocksContainer = document.getElementById('fallBlocksContainer');
    fallBlocksContainer.classList.toggle('hidden', !this.checked);
});

document.getElementById('createExplosionsEnabled').addEventListener('change', function() {
    const createExplosionsContainer = document.getElementById('createExplosionsContainer');
    createExplosionsContainer.classList.toggle('hidden', !this.checked);
});

document.getElementById('spawnMobsEnabled').addEventListener('change', function() {
    const spawnMobsContainer = document.getElementById('spawnMobsContainer');
    spawnMobsContainer.classList.toggle('hidden', !this.checked);
});

document.getElementById('givePotionEffectsEnabled').addEventListener('change', function() {
    const givePotionEffectsContainer = document.getElementById('givePotionEffectsContainer');
    givePotionEffectsContainer.classList.toggle('hidden', !this.checked);
});

document.getElementById('sendMessagesEnabled').addEventListener('change', function() {
    const sendMessagesContainer = document.getElementById('sendMessagesContainer');
    sendMessagesContainer.classList.toggle('hidden', !this.checked);
});

document.getElementById('addParticlesEnabled').addEventListener('change', function() {
    const addParticlesContainer = document.getElementById('addParticlesContainer');
    addParticlesContainer.classList.toggle('hidden', !this.checked);
});

document.addEventListener('change', function(event) {
    if (event.target.name === 'useRandom') {
        const parentDiv = event.target.closest('.form-group');
        const randomMinInput = parentDiv.querySelector('input[name="randomMin"]');
        const randomMaxInput = parentDiv.querySelector('input[name="randomMax"]');
        const numInput = parentDiv.querySelector('input[name="num"]');
        
        if (event.target.checked) {
            randomMinInput.closest('.form-group').classList.remove('hidden');
            randomMaxInput.closest('.form-group').classList.remove('hidden');
            numInput.closest('.form-group').classList.add('hidden');
        } else {
            randomMinInput.closest('.form-group').classList.add('hidden');
            randomMaxInput.closest('.form-group').classList.add('hidden');
            numInput.closest('.form-group').classList.remove('hidden');
        }
    }
    if (event.target.name === 'mobUseRandom') {
        const parentDiv = event.target.closest('.form-group');
        const randomMinInput = parentDiv.querySelector('input[name="mobRandomMin"]');
        const randomMaxInput = parentDiv.querySelector('input[name="mobRandomMax"]');
        const numInput = parentDiv.querySelector('input[name="mobNum"]');

        if (event.target.checked) {
            randomMinInput.closest('.form-group').classList.remove('hidden');
            randomMaxInput.closest('.form-group').classList.remove('hidden');
            numInput.closest('.form-group').classList.add('hidden');
        } else {
            randomMinInput.closest('.form-group').classList.add('hidden');
            randomMaxInput.closest('.form-group').classList.add('hidden');
            numInput.closest('.form-group').classList.remove('hidden');
        }
    }
});

function addDropItem() {
    const dropItemsList = document.getElementById('dropItemsList');
    const dropItemDiv = document.createElement('div');
    dropItemDiv.classList.add('form-group');
    dropItemDiv.innerHTML = `
        <label>Drop Item ID:</label>
        <input type="text" name="dropItemId" value="minecraft:">
        <input type="checkbox" name="useRandom">
        <span id="span_lable">Use Random Number</span>
        <div class="form-group hidden">
            <label>Random Min:</label>
            <input type="number" name="randomMin">
        </div>
        <div class="form-group hidden">
            <label>Random Max:</label>
            <input type="number" name="randomMax">
        </div>
        <div class="form-group">
            <label>Number:</label>
            <input type="number" name="num">
        </div>
        <label>Position Source:</label>
        <select name="dropPosSrc">
            <option value="0">Block</option>
            <option value="1">Player</option>
        </select>
        <label>Offset (x, y, z):</label>
        <input type="text" name="dropOffset">
        <button id="remove" type="button" onclick="removeElement(this)">Remove</button>
    `;
    dropItemsList.appendChild(dropItemDiv);
}

function addPlaceBlock() {
    const placeBlocksList = document.getElementById('placeBlocksList');
    const placeBlockDiv = document.createElement('div');
    placeBlockDiv.classList.add('form-group');
    placeBlockDiv.innerHTML = `
        <label>Block ID:</label>
        <input type="text" name="placeBlockId" value="minecraft:">
        <label>Position Source:</label>
        <select name="placePosSrc">
            <option value="0">Block</option>
            <option value="1">Player</option>
        </select>
        <label>Offset (x, y, z):</label>
        <input type="text" name="placeOffset">
        <button id="remove" type="button" onclick="removeElement(this)">Remove</button>
    `;
    placeBlocksList.appendChild(placeBlockDiv);
}

function addPlaceChest() {
    const placeChestsList = document.getElementById('placeChestsList');
    const placeChestDiv = document.createElement('div');
    placeChestDiv.classList.add('form-group');
    placeChestDiv.innerHTML = `
        <label>Loot Table ID:</label>
        <input type="text" name="placeChestId" value="chests/">
        <label>Chest Type:</label>
        <select name="chestType">
            <option value="0">Chest</option>
            <option value="1">Trapped Chest</option>
            <option value="2">Barrel</option>
            <option value="3">Shulker Box</option>
        </select>
        <label>Position Source:</label>
        <select name="placePosSrc">
            <option value="0">Block</option>
            <option value="1">Player</option>
        </select>
        <label>Offset (x, y, z):</label>
        <input type="text" name="placeOffset">
        <button id="remove" type="button" onclick="removeElement(this)">Remove</button>
    `;
    placeChestsList.appendChild(placeChestDiv);
}

function addFallBlock() {
    const fallBlocksList = document.getElementById('fallBlocksList');
    const fallBlockDiv = document.createElement('div');
    fallBlockDiv.classList.add('form-group');
    fallBlockDiv.innerHTML = `
        <label>Block ID:</label>
        <input type="text" name="fallBlockId" value="minecraft:">
        <label>Position Source:</label>
        <select name="fallPosSrc">
            <option value="0">Block</option>
            <option value="1">Player</option>
        </select>
        <label>Offset (x, y, z):</label>
        <input type="text" name="fallOffset">
        <button id="remove" type="button" onclick="removeElement(this)">Remove</button>
    `;
    fallBlocksList.appendChild(fallBlockDiv);
}

function addCreateExplosion() {
    const createExplosionsList = document.getElementById('createExplosionsList');
    const createExplosionDiv = document.createElement('div');
    createExplosionDiv.classList.add('form-group');
    createExplosionDiv.innerHTML = `
        <label>Explosion Power:</label>
        <input type="number" name="explosionPower">
        <label>Position Source:</label>
        <select name="explosionPosSrc">
            <option value="0">Block</option>
            <option value="1">Player</option>
        </select>
        <label>Offset (x, y, z):</label>
        <input type="text" name="explosionOffset">
        <button id="remove" type="button" onclick="removeElement(this)">Remove</button>
    `;
    createExplosionsList.appendChild(createExplosionDiv);
}

function addSpawnMob() {
    const spawnMobsList = document.getElementById('spawnMobsList');
    const spawnMobDiv = document.createElement('div');
    spawnMobDiv.classList.add('form-group');
    spawnMobDiv.innerHTML = `
        <label>Mob ID:</label>
        <input type="text" name="mobId" value="minecraft:">
        <label>Position Source:</label>
        <select name="mobPosSrc">
            <option value="0">Block</option>
            <option value="1">Player</option>
        </select>
        <label>Offset (x, y, z):</label>
        <input type="text" name="mobOffset">
        <label>Mob Name:</label>
        <input type="text" name="mobName">
        <input type="checkbox" name="mobNameVisible">
        <span id="span_lable">Name Visible</span>
        <br>
        <input type="checkbox" name="mobIsBaby">
        <span id="span_lable">Is Baby</span>
        <br>
        <input type="checkbox" name="mobUseRandom">
        <span id="span_lable">Use Random Number</span>
        <div class="form-group hidden">
            <label>Random Min:</label>
            <input type="number" name="mobRandomMin">
        </div>
        <div class="form-group hidden">
            <label>Random Max:</label>
            <input type="number" name="mobRandomMax">
        </div>
        <div class="form-group">
            <label>Number:</label>
            <input type="number" name="mobNum">
        </div>
        <label>NBT Data:</label>
        <input type="text" name="mobNBT">
        <button id="remove" type="button" onclick="removeElement(this)">Remove</button>
    `;
    spawnMobsList.appendChild(spawnMobDiv);
}

function addGivePotionEffect() {
    const givePotionEffectsList = document.getElementById('givePotionEffectsList');
    const givePotionEffectDiv = document.createElement('div');
    givePotionEffectDiv.classList.add('form-group');
    givePotionEffectDiv.innerHTML = `
        <label>Potion Effect ID:</label>
        <input type="text" name="potionEffectId" value="minecraft:">
        <label>Amplifier:</label>
        <input type="number" name="potionAmplifier">
        <label>Duration:</label>
        <input type="number" name="potionDuration">
        <button id="remove" type="button" onclick="removeElement(this)">Remove</button>
    `;
    givePotionEffectsList.appendChild(givePotionEffectDiv);
}

function addSendMessage() {
    const sendMessagesList = document.getElementById('sendMessagesList');
    const sendMessageDiv = document.createElement('div');
    sendMessageDiv.classList.add('form-group');
    sendMessageDiv.innerHTML = `
        <label>Message:</label>
        <input type="text" name="messageContent">
        <label>Receiver:</label>
        <select name="messageReceiver">
            <option value="0">Inventory</option>
            <option value="1">Chat</option>
        </select>
        <button id="remove" type="button" onclick="removeElement(this)">Remove</button>
    `;
    sendMessagesList.appendChild(sendMessageDiv);
}

function addAddParticle() {
    const addParticlesList = document.getElementById('addParticlesList');
    const addParticleDiv = document.createElement('div');
    addParticleDiv.classList.add('form-group');
    addParticleDiv.innerHTML = `
        <label>Particle Id:</label>
        <input type="text" name="particleId">
        <label>Count:</label>
        <input type="number" name="count">
        <label>Velocity (x, y, z):</label>
        <input type="text" name="particleVelocity">
        <label>Speed:</label>
        <input type="number" name="speed">
        <label>Position Source:</label>
        <select name="particlePosSrc">
            <option value="0">Block</option>
            <option value="1">Player</option>
        </select>
        <label>Offset (x, y, z):</label>
        <input type="text" name="particleOffset">
        <button id="remove" type="button" onclick="removeElement(this)">Remove</button>
    `;
    addParticlesList.appendChild(addParticleDiv);
}

function removeElement(button) {
    button.parentElement.remove();
}

function generateJSON() {
    const form = document.getElementById('randomEventsForm');
    const formData = new FormData(form);

    const eventId = formData.get('eventId');
    const randomEvent = { id: parseInt(eventId) };

    if (formData.get('dropItemsEnabled') === 'on') {
        randomEvent.drop_items = getDropItems(formData);
    }
    if (formData.get('placeBlocksEnabled') === 'on') {
        randomEvent.place_blocks = getPlaceBlocks(formData);
    }
    if (formData.get('placeChestsEnabled') === 'on') {
        randomEvent.place_chests = getPlaceChests(formData);
    }
    if (formData.get('fallBlocksEnabled') === 'on') {
        randomEvent.fall_blocks = getFallBlocks(formData);
    }
    if (formData.get('createExplosionsEnabled') === 'on') {
        randomEvent.create_explosions = getCreateExplosions(formData);
    }
    if (formData.get('spawnMobsEnabled') === 'on') {
        randomEvent.spawn_mobs = getSpawnMobs(formData);
    }
    if (formData.get('givePotionEffectsEnabled') === 'on') {
        randomEvent.give_potion_effects = getGivePotionEffects(formData);
    }
    if (formData.get('sendMessagesEnabled') === 'on') {
        randomEvent.send_messages = getSendMessages(formData);
    }
    if (formData.get('addParticlesEnabled') === 'on') {
        randomEvent.add_particles = getAddParticles(formData);
    }

    document.getElementById('generatedJSON').textContent = JSON.stringify(randomEvent, null, 4);
}

function getDropItems(formData) {
    const dropItemIds = formData.getAll('dropItemId');
    const useRandoms = formData.getAll('useRandom');
    const randomMins = formData.getAll('randomMin');
    const randomMaxs = formData.getAll('randomMax');
    const nums = formData.getAll('num');
    const dropPosSrcs = formData.getAll('dropPosSrc');
    const dropOffsets = formData.getAll('dropOffset');

    return dropItemIds.map((id, i) => {
        const offsetValues = dropOffsets[i] ? dropOffsets[i].split(',').map(Number) : [0.0, 0.0, 0.0];
        const dropItem = {
            id,
            use_random: useRandoms[i] === 'on'
        };

        if (dropItem.use_random) {
            dropItem.random_num = {
                min: parseInt(randomMins[i]) || 0,
                max: parseInt(randomMaxs[i]) || 1
            };
        } else {
            dropItem.num = parseInt(nums[i]) || 1;
        }

        if (dropPosSrcs[i]) {
            dropItem.pos_src = parseInt(dropPosSrcs[i]);
        }

        if (dropOffsets[i]) {
            dropItem.offset = {
                x: offsetValues[0] || 0.0,
                y: offsetValues[1] || 0.0,
                z: offsetValues[2] || 0.0
            };
        }

        return dropItem;
    });
}

function getPlaceBlocks(formData) {
    const placeBlockIds = formData.getAll('placeBlockId');
    const placePosSrcs = formData.getAll('placePosSrc');
    const placeOffsets = formData.getAll('placeOffset');

    return placeBlockIds.map((id, i) => {
        const offsetValues = placeOffsets[i] ? placeOffsets[i].split(',').map(Number) : [0.0, 0.0, 0.0];
        const placeBlock = {
            id
        };

        if (placePosSrcs[i]) {
            placeBlock.pos_src = parseInt(placePosSrcs[i]) || 0;
        }

        if (placeOffsets[i]) {
            placeBlock.offset = {
                x: offsetValues[0] || 0.0,
                y: offsetValues[1] || 0.0,
                z: offsetValues[2] || 0.0
            };
        }

        return placeBlock;
    });
}

function getPlaceChests(formData) {
    const placeChestIds = formData.getAll('placeChestId');
    const placeChestTypes = formData.getAll('chestType');
    const placePosSrcs = formData.getAll('placePosSrc');
    const placeOffsets = formData.getAll('placeOffset');

    return placeChestIds.map((id, i) => {
        const offsetValues = placeOffsets[i] ? placeOffsets[i].split(',').map(Number) : [0.0, 0.0, 0.0];
        const placeChest = {
            id
        };

        if (placeChestTypes[i]) {
            placeChest.type = parseInt(placeChestTypes[i]) || 0;
        }

        if (placePosSrcs[i]) {
            placeChest.pos_src = parseInt(placePosSrcs[i]) || 0;
        }

        if (placeOffsets[i]) {
            placeChest.offset = {
                x: offsetValues[0] || 0.0,
                y: offsetValues[1] || 0.0,
                z: offsetValues[2] || 0.0
            };
        }

        return placeChest;
    });
}

function getFallBlocks(formData) {
    const fallBlockIds = formData.getAll('fallBlockId');
    const fallPosSrcs = formData.getAll('fallPosSrc');
    const fallOffsets = formData.getAll('fallOffset');

    return fallBlockIds.map((id, i) => {
        const offsetValues = fallOffsets[i] ? fallOffsets[i].split(',').map(Number) : [0, 0, 0];
        const fallBlock = {
            id
        };

        if (fallPosSrcs[i]) {
            fallBlock.pos_src = parseInt(fallPosSrcs[i]) || 0;
        }

        if (fallOffsets[i]) {
            fallBlock.offset = {
                x: offsetValues[0] || 0.0,
                y: offsetValues[1] || 0.0,
                z: offsetValues[2] || 0.0
            };
        }

        return fallBlock;
    });
}

function getCreateExplosions(formData) {
    const explosionPowers = formData.getAll('explosionPower');
    const explosionPosSrcs = formData.getAll('explosionPosSrc');
    const explosionOffsets = formData.getAll('explosionOffset');

    return explosionPowers.map((power, i) => {
        const offsetValues = explosionOffsets[i] ? explosionOffsets[i].split(',').map(Number) : [0.0, 0.0, 0.0];
        const explosion = {
            power: parseInt(power) || 1
        };

        if (explosionPosSrcs[i]) {
            explosion.pos_src = parseInt(explosionPosSrcs[i]) || 0;
        }

        if (explosionOffsets[i]) {
            explosion.offset = {
                x: offsetValues[0] || 0.0,
                y: offsetValues[1] || 0.0,
                z: offsetValues[2] || 0.0
            };
        }

        return explosion;
    });
}

function getSpawnMobs(formData) {
    const mobIds = formData.getAll('mobId');
    const mobPosSrcs = formData.getAll('mobPosSrc');
    const mobOffsets = formData.getAll('mobOffset');
    const mobNames = formData.getAll('mobName');
    const mobNameVisibles = formData.getAll('mobNameVisible');
    const mobIsBabys = formData.getAll('mobIsBaby');
    const mobUseRandoms = formData.getAll('mobUseRandom');
    const mobRandomMins = formData.getAll('mobRandomMin');
    const mobRandomMaxs = formData.getAll('mobRandomMax');
    const mobNums = formData.getAll('mobNum');
    const mobNBTs = formData.getAll('mobNBT');

    return mobIds.map((id, i) => {
        const offsetValues = mobOffsets[i] ? mobOffsets[i].split(',').map(Number) : [0.0, 0.0, 0.0];
        const mob = {
            id,
            name: mobNames[i] || null,
            name_visible: mobNameVisibles[i] === 'on',
            is_baby: mobIsBabys[i] === 'on',
            use_random: mobUseRandoms[i] === 'on'
        };

        if (mob.use_random) {
            mob.random_num = {
                min: parseInt(mobRandomMins[i]) || 0,
                max: parseInt(mobRandomMaxs[i]) || 1
            };
        } else {
            mob.num = parseInt(mobNums[i]) || 1;
        }

        if (mobPosSrcs[i]) {
            mob.pos_src = parseInt(mobPosSrcs[i]) || 0;
        }

        if (mobOffsets[i]) {
            mob.offset = {
                x: offsetValues[0] || 0.0,
                y: offsetValues[1] || 0.0,
                z: offsetValues[2] || 0.0
            };
        }

        if (mobNBTs[i]) {
            mob.nbt = mobNBTs[i];
        }

        return mob;
    });
}

function getGivePotionEffects(formData) {
    const potionEffectIds = formData.getAll('potionEffectId');
    const potionAmplifiers = formData.getAll('potionAmplifier');
    const potionDurations = formData.getAll('potionDuration');

    return potionEffectIds.map((id, i) => ({
        id,
        amplifier: parseInt(potionAmplifiers[i]) || 0,
        duration: parseInt(potionDurations[i]) || 1
    }));
}

function getSendMessages(formData) {
    const messageContents = formData.getAll('messageContent');
    const messageReceivers = formData.getAll('messageReceiver');

    return messageContents.map((msg, i) => ({
        msg,
        receiver: parseInt(messageReceivers[i]) || 0
    }));
}

function getAddParticles(formData) {
    const particleIds = formData.getAll('particleId');
    const particleCounts = formData.getAll('count');
    const particleVelocitys = formData.getAll('particleVelocity');
    const particleSpeeds = formData.getAll('speed');
    const particlePosSrcs = formData.getAll('particlePosSrc');
    const particleOffsets = formData.getAll('particleOffset');

    return particleIds.map((id, i) => {
        const velocityValues = particleVelocitys[i] ? particleVelocitys[i].split(',').map(Number) : [1.0, 1.0, 1.0];
        const offsetValues = particleOffsets[i] ? particleOffsets[i].split(',').map(Number) : [0.0, 0.0, 0.0];
        const particle = {
            id
        };

        if (particleCounts[i]) {
            particle.count = parseInt(particleCounts[i]) || 1;
        }

        if (particleSpeeds[i]) {
            particle.speed = parseFloat(particleSpeeds[i]) || 0.0;
        }

        if (particleVelocitys[i]) {
            particle.velocity = {
                x: velocityValues[0] || 1.0,
                y: velocityValues[1] || 1.0,
                z: velocityValues[2] || 1.0
            };
        }

        if (particlePosSrcs[i]) {
            particle.pos_src = parseInt(particlePosSrcs[i]) || 0;
        }

        if (particleOffsets[i]) {
            particle.offset = {
                x: offsetValues[0] || 0.0,
                y: offsetValues[1] || 0.0,
                z: offsetValues[2] || 0.0
            };
        }

        return particle;
    });
}