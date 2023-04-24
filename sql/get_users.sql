create or replace function get_relations(roomId text)
    returns table
            (
                giver     text,
                recipient text
            )
as
$$
begin
    return query select giver.name     as giver,
                        recipient.name as recipient
                 from room_members
                          inner join users giver on giver.user_id = room_members.user_id
                          inner join users recipient on recipient.user_id = room_members.recipient
                 where room_members.room_id = roomId;
end;
$$ language plpgsql;

select *
from get_relations('room_for_my_homies2');

select *
from get_relations('room_for_my_homies');
