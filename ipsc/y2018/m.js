/*=====*/
t = customer.text;
v1 = customer.var1;

n1 = parseInt(v1, 2);
console.log("TASK:" + t);
ans = "";
if (t == "HELLO I WOULD LIKE TO ORDER FOOD ITEM var1.") {
	ans = v1;
}
if (t == "GIVE ME ANY FOOD BETWEEN 1 AND 1000, INCLUSIVE. BUT NOT var1, I HATE THAT.") {
	ans = "1";
	if (ans == v1) {
		ans = "10";
	}
}
if (t == "I HEARD var1 IS VERY TASTY, BUT I DO NOT UNDERSTAND THAT FOREIGN LANGUAGE. WHAT IS IT CALLED IN BINARY?") {
	x = eval(v1);
	ans = "" + x.toString(2);
}
if (t == "I WANT THE SQUARE ROOT OF var1.") {
	x = Math.round(Math.sqrt(n1));
	ans = "" + x.toString(2);
}
if (t == "OLLEH, I MA A BOTOR TAHT SKLAT SDRAWKCAB. EVIG EM DOOF var1 ESAELP.") {
	ans = v1.split("").reverse().join("");
}
if (t == "I AM SO EXCITED! GIVE ME var1 AND YES, OF COURSE THAT IS A FACTORIAL!") {
	v1 = v1.substring(0, v1.length - 1);
	n1 = parseInt(v1, 2);
	f = 1;
	for (i = 1; i <= n1; i++) {
		f *= i;
	}
	ans = "" + f.toString(2);
}
if (t == "GOOD DAY COUGH GIVE ME FOOD var1 COUGH COUGH COUGH. PARDON ME, THAT WAS A BUFFER OVERFLOW. I WANTED TO SAY THE FIRST 110 DIGITS OF THAT, PLEASE IGNORE THE REST.") {
	ans = v1.substring(0, 6);
}
if (t == "I JUST WANT TO KNOW: IS var1 CORRECTLY NESTED? PLEASE ANSWER YES OR NO.") {
	b = "";
	for (i = 0; i < v1.length; i++) {
		ok = 1;
		if (v1[i] == "(" || v1[i] == "[") {
			b += v1[i];
			continue;
		}
		if (b == "") {
			ok = 0;
			break;
		}
		last = b[b.length - 1];
		if (v1[i] == ")" && last != "(" || v1[i] == "]" && last != "[") {
			ok = 0;
			break;
		}
		b = b.substring(0, b.length - 1);
	}
	if (b != "") {
		ok = 0;
	}
	if (ok == 1) {ans = "YES";} else {ans = "NO";}
}
if (t == "A STRANGE GAME. THE ONLY WINNING MOVE IS NOT TO PLAY.\nBUT WHO WON? ANSWER X OR O OR NEITHER.\n\nvar1") {
	b = v1.split("\n");
	ans = "NEITHER";
	s1 = b[0][0] + b[0][1] + b[0][2];
	s2 = b[1][0] + b[1][1] + b[1][2];
	s3 = b[2][0] + b[2][1] + b[2][2];
	s4 = b[0][0] + b[1][0] + b[2][0];
	s5 = b[0][1] + b[1][1] + b[2][1];
	s6 = b[0][2] + b[1][2] + b[2][2];
	s7 = b[0][0] + b[1][1] + b[2][2];
	s8 = b[0][2] + b[1][1] + b[2][0];
	ss = [s1, s2, s3, s4, s5, s6, s7, s8];
	if (ss.includes('XXX')) {
		ans = "X";
	}
	if (ss.includes('OOO')) {
		ans = "O";
	}
	if (ss.includes('XXX') && ss.includes('OOO')) {
		ans = "";
	}
}

console.log("ANSWER:" + ans);
if (ans != "") {
	M.submitAnswer(id, ans);
}
/*=====*/
