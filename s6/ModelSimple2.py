from tkinter import *

WIDTH = 1000
HEIGHT = 300

PAD_W = 20
PAD_H = HEIGHT

BALL_SPEED_UP = 1
BALL_MAX_SPEED = 400
BALL_RADIUS = 30

INITIAL_SPEED = 40
BALL_X_SPEED = INITIAL_SPEED
BALL_Y_SPEED = 0

PLAYER_1_SCORE = 0
PLAYER_2_SCORE = 10

BALLS = []

# Добавим глобальную переменную отвечающую за расстояние
# до правого края игрового поля
right_line_distance = WIDTH - PAD_W


def add_score(player, ball):
    for b in BALLS:
        print(c.coords(b))
    global PLAYER_1_SCORE, PLAYER_2_SCORE
    if player == "right":
        PLAYER_1_SCORE += 1
        c.itemconfig(p_1_text, text=PLAYER_1_SCORE)
    else:
        PLAYER_2_SCORE += 1
        c.itemconfig(p_2_text, text=PLAYER_2_SCORE)
    BALLS.remove(ball)


def reduce_score(player):
    global PLAYER_1_SCORE, PLAYER_2_SCORE
    if player == "left-to-right":
        PLAYER_1_SCORE -= 1
        c.itemconfig(p_1_text, text=PLAYER_1_SCORE)
    if player == "right-to-left":
        PLAYER_2_SCORE -= 1
        c.itemconfig(p_2_text, text=PLAYER_2_SCORE)


def spawn_ball(way):
    global BALL_X_SPEED
    if way == "left-to-right":
        if PLAYER_1_SCORE < 1:
            return
    if way == "right-to-left":
        if PLAYER_2_SCORE < 1:
            return
    ball = c.create_oval(WIDTH / 2 - BALL_RADIUS / 2,
                         HEIGHT / 2 - BALL_RADIUS / 2,
                         WIDTH / 2 + BALL_RADIUS / 2,
                         HEIGHT / 2 + BALL_RADIUS / 2, fill="white")
    reduce_score(way)
    if way == "right-to-left":
        c.coords(ball, 0 + PAD_W - BALL_RADIUS / 2,
                 HEIGHT / 2 - BALL_RADIUS / 2,
                 0 + PAD_W + BALL_RADIUS / 2,
                 HEIGHT / 2 + BALL_RADIUS / 2)
        BALL_X_SPEED = INITIAL_SPEED

    if way == "left-to-right":
        c.coords(ball, WIDTH - PAD_W - BALL_RADIUS / 2,
                 HEIGHT / 2 - BALL_RADIUS / 2,
                 WIDTH - PAD_W + BALL_RADIUS / 2,
                 HEIGHT / 2 + BALL_RADIUS / 2)

        BALL_X_SPEED = -INITIAL_SPEED
    BALLS.append(ball)


# устанавливаем окно
root = Tk()
root.title("Simple Model")

# область анимации
c = Canvas(root, width=WIDTH, height=HEIGHT, background="#003300")
c.pack()
c.create_line(PAD_W, 0, PAD_W, HEIGHT, fill="white")
c.create_line(WIDTH - PAD_W, 0, WIDTH - PAD_W, HEIGHT, fill="white")
c.create_line(WIDTH / 2, 0, WIDTH / 2, HEIGHT, fill="white")

LEFT_PAD = c.create_line(PAD_W / 2, 0, PAD_W / 2, PAD_H, width=PAD_W, fill="yellow")
RIGHT_PAD = c.create_line(WIDTH - PAD_W / 2, 0, WIDTH - PAD_W / 2, PAD_H, width=PAD_W, fill="yellow")

p_1_text = c.create_text(WIDTH - WIDTH / 6, PAD_H / 4, text=PLAYER_1_SCORE, font="Arial 20", fill="white")
p_2_text = c.create_text(WIDTH / 6, PAD_H / 4, text=PLAYER_2_SCORE, font="Arial 20", fill="white")


def move_ball(ball):
    # определяем координаты сторон мяча и его центра
    ball_left, ball_top, ball_right, ball_bot = c.coords(ball)
    ball_center = (ball_top + ball_bot) / 2

    # вертикальный отскок
    # Если мы далеко от вертикальных линий - просто двигаем мяч
    if ball_right + BALL_X_SPEED < right_line_distance and \
                            ball_left + BALL_X_SPEED > PAD_W:
        c.move(ball, BALL_X_SPEED, BALL_Y_SPEED)
    # Если мяч касается своей правой или левой стороной границы поля
    elif ball_right == right_line_distance or ball_left == PAD_W:
        if ball_right > WIDTH / 2:
            if c.coords(RIGHT_PAD)[1] < ball_center < c.coords(RIGHT_PAD)[3]:
                add_score("right", ball)
        else:
            if c.coords(LEFT_PAD)[1] < ball_center < c.coords(LEFT_PAD)[3]:
                add_score("left", ball)

    else:
        if ball_right > WIDTH / 2:
            c.move(ball, right_line_distance - ball_right, BALL_Y_SPEED)
        else:
            c.move(ball, -ball_left + PAD_W, BALL_Y_SPEED)


def move_balls():
    if len(BALLS) == 0:
        return
    for ball in BALLS:
        print(ball)
        move_ball(ball)

# зададим глобальные переменные скорости движения ракеток
# скорось с которой будут ездить ракетки
PAD_SPEED = 5
# скорость левой ракетки
LEFT_PAD_SPEED = 200
# скорость правой ракетки
RIGHT_PAD_SPEED = 0


def main():
    move_balls()
    root.after(30, main)


# Установим фокус на Canvas чтобы он реагировал на нажатия клавиш
c.focus_set()


# Напишем функцию обработки нажатия клавиш
def movement_handler(event):
    global LEFT_PAD_SPEED, RIGHT_PAD_SPEED
    if event.keysym == "a":
        spawn_ball("left-to-right")
    elif event.keysym == "d":
        spawn_ball("right-to-left")


# Привяжем к Canvas эту функцию
c.bind("<KeyPress>", movement_handler)


# Создадим функцию реагирования на отпускание клавиши
def stop_pad(event):
    global LEFT_PAD_SPEED, RIGHT_PAD_SPEED
    if event.keysym in "ws":
        LEFT_PAD_SPEED = 0
    elif event.keysym in ("Up", "Down"):
        RIGHT_PAD_SPEED = 0


# Привяжем к Canvas эту функцию
c.bind("<KeyRelease>", stop_pad)

# запускаем движение
main()

# запускаем работу окна
root.mainloop()
