import pygame
import numpy as np
import math

pygame.init()

WIDTH, HEIGHT = 1200, 1000

screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Doom Clone")

# Colors
WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
SKY = (135, 206, 235)
GROUND = (100, 100, 100)
RED = (255, 0, 0)
BLUE = (0, 0, 255)
GREEN = (0, 255, 0)
YELLOW = (255, 255, 0)

colors = [RED, BLUE, GREEN, YELLOW, WHITE]


CASTED_RAYS = 180
FOV = np.pi / 2
HALF_FOV = FOV / 2
STEP_ANGLE = FOV / WIDTH

playerX, playerY = 250, 250
playerAngle = 0

mapSize = 8
MAX_DEPTH = 2000
game_map = [
    [4] * mapSize,
    [1] + [0] * (mapSize - 2) + [2],
    [1] + [0] * (mapSize - 2) + [2],
    [1, 0, 0, 0, 5, 0, 0, 2],
    [1, 0, 0, 5, 0, 0, 0, 2],
    [1, 0, 0, 0, 0, 0, 0, 2],
    [1] + [0] * (mapSize - 2) + [2],
    [3] * mapSize
]


def cast_rays():
    startAngle = playerAngle - HALF_FOV
    for ray in range(CASTED_RAYS):
        for depth in range(0, MAX_DEPTH, 50):
            targetX = playerX - math.sin(startAngle) * depth
            targetY = playerY + math.cos(startAngle) * depth

            row = int(targetX / 100)
            col = int(targetY / 100)

            # print(row, col)
            if game_map[row][col] != 0:
                # color = 255 / (1 + depth * depth * 100 * 0.0001)
                color = colors[game_map[row][col] - 1]
                depth *= math.cos(playerAngle - startAngle)
                wall_height = 50000 / (depth + 0.0001)

                wall_height = min(wall_height, HEIGHT)
                
                pygame.draw.rect(screen, color, (ray * WIDTH / CASTED_RAYS, (HEIGHT / 2) - wall_height / 2, WIDTH / CASTED_RAYS, wall_height))
                break
        startAngle += STEP_ANGLE


running = True
clock = pygame.time.Clock()

while running:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False
        
    keys = pygame.key.get_pressed()
    if keys[pygame.K_LEFT]:
        playerAngle -= 0.01
    if keys[pygame.K_RIGHT]:
        playerAngle += 0.01
    if keys[pygame.K_UP]:
        playerY += math.sin(playerAngle) * .1
        playerX += math.cos(playerAngle) * .1
    if keys[pygame.K_DOWN]:
        playerY -= math.sin(playerAngle) * .1
        playerX -= math.cos(playerAngle) * .1
    screen.fill(SKY)
    pygame.draw.rect(screen, GROUND, (0, HEIGHT / 2, WIDTH, HEIGHT / 2))

    cast_rays()

    pygame.display.flip()
    clock.tick(60)


pygame.quit()



# pygame.draw.rect(screen, (color, color, color), (ray * WIDTH / CASTED_RAYS, (HEIGHT / 2) - wall_height / 2, WIDTH / CASTED_RAYS, wall_height))