xor_others = 0
for i = 1, N do
  if i ~= myself then
    xor_others = xor_others ~ colors[i]
  end
end

if myself == N then
  ans = 0
  for i = 0, N - 2 do
    ans = (ans ~ (xor_others >> i)) & 1
  end
  ans = ans ~= 0
else
  ans = (xor_others & (1 << (myself - 1))) ~= 0
end

hands = raise(ans)

answer = 0
for i = 1, N do
  if i ~= myself then
    h = hands[i]
    bit = i - 1
    if i == N then
      bit = myself - 1
      for j = 0, N-2 do
        if j ~= bit then
          h = h ~= (((xor_others ~ colors[i] ~ answer) & (1 << j)) ~= 0)
        end
      end
    end
    if h then
      h = 1 << bit
    else
      h = 0
    end
    answer = answer | ((xor_others ~ colors[i] ~ h) & (1 << bit))
  end
end
