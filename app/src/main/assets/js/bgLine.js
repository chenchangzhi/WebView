var w = c.width = window.innerWidth,
	h = c.height = window.innerHeight,
	ctx = c.getContext('2d'),

	count = (w * h / 3000) | 0,
	speed = 10, //变换速度
	range = 100,
	lineAlpha = 0.5,

	particles = [],
	huePart = 360 / count;

for(var i = 0; i < count; ++i)
	particles.push(new Particle((huePart * i) | 0));

function Particle(hue) {
	this.x = Math.random() * w;
	this.y = Math.random() * h;
	this.vx = (Math.random() - .5) * speed;
	this.vy = (Math.random() - .5) * speed;

	this.hue = hue;
}
Particle.prototype.update = function() {
	this.x += this.vx;
	this.y += this.vy;

	if(this.x < 0 || this.x > w) this.vx *= -1;
	if(this.y < 0 || this.y > h) this.vy *= -1;
}

function checkDist(a, b, dist) {
	var x = a.x - b.x,
		y = a.y - b.y;

	return x * x + y * y <= dist * dist;
}

function anim() {
	window.requestAnimationFrame(anim);

	ctx.fillStyle = 'rgba(255, 255, 255, 1.0)'; //背景色和透明度
	ctx.fillRect(0, 0, w, h);

	for(var i = 0; i < particles.length; ++i) {
		var p1 = particles[i];
		p1.update();

		for(var j = i + 1; j < particles.length; ++j) {
			var p2 = particles[j];
			if(checkDist(p1, p2, range)) {
				ctx.strokeStyle = 'hsla(hue, 50%, 50%, alp)'
					.replace('hue', ((p1.hue + p2.hue + 3) / 2) % 360)
					.replace('alp', lineAlpha);
				ctx.beginPath();
				ctx.moveTo(p1.x, p1.y);
				ctx.lineTo(p2.x, p2.y);
				ctx.stroke();
			}
		}
	}
}

anim();

function reloadData(width, height) {
	this.w = width;
	this.h = height;
	this.count = (w * h / 3000) | 0;
	this.particles = [];
	this.huePart = 360 / count;
	this.particles = [];
	for(var i = 0; i < count; ++i)
		this.particles.push(new Particle((huePart * i) | 0));
	anim();
}